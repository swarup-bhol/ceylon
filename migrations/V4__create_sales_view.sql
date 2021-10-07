create or replace function profit_calc(buy numeric, sell numeric, buy_currency varchar)
    returns record
as $body$
declare
    base varchar := 'EUR';
    e_rate numeric := 1.0;
    out record;
begin
    select cr.id into base from currency cr where cr.id = (select value from system_parameters sp where sp.name = 'BASE_CURRENCY' limit 1);
    if base = buy_currency then
        select 1.0 into e_rate;
    else
        select rate into e_rate from exchange_rate where from_currency = buy_currency and to_currency = base;
    end if;

    select
        case when sell is null or sell = 0 then 0 else (sell::numeric - (buy * e_rate)::numeric) end as profit,
        buy,
        sell,
        round(buy::numeric * e_rate::numeric, 2) as buy_in_base,
        case when sell is null or sell = 0 then 0 else  round(((sell - (buy * e_rate)) * 100 /sell)::numeric, 2 ) end as profit_percentage
    into out;
    return out;
end;
$body$ language plpgsql;


create or replace function to_base_currency(price numeric, currency varchar)
    returns numeric
as $body$
declare
    base varchar := 'EUR';
    e_rate numeric := 1.0;
begin
    select cr.id into base from currency cr where cr.id = (select value from system_parameters sp where sp.name = 'BASE_CURRENCY' limit 1);
    if base = currency then
        select 1.0 into e_rate;
    else
        select rate into e_rate from exchange_rate where from_currency = currency and to_currency = base;
    end if;
    return price * e_rate;
end;
$body$ language plpgsql;

drop table if exists sales;

create or replace  view  sales as
select
    extract( year from to_date(invoice.date, 'YYYY-MM-DD')) as year,
    invoice.state as status,
    invoice.date as invoice_date,
    invoice.due_date as payment_target,
    invoice.completed_on as transfer_date,
    extract( month from to_date(invoice.completed_on, 'YYYY-MM-DD')) as month_transfer,
    extract( quarter from to_date(invoice.completed_on, 'YYYY-MM-DD')) as quarter_transfer,
    null as contact,
    invoice.id as invoice_no,
    invoice.id as id,
    c.company,
    concat_ws(' ', c.title, c.first_name, c.name) as name,
    c.country,
    c.type,
    c.poc_lv1,
    c.poc_lv2,
    'N/A' as pos_lv1,
    'N/A' as pos_lv2,
    'N/A' as rtb,
    'N/A' as fair_trade,
    array_to_string(array_agg(concat_ws('',i.qty, 'x ',p.goods,' ', i.cts::numeric, 'cts')), ',') as goods,
    sum(i.qty) as qty,
    sum(i.cts::numeric) as cts,
    invoice.vat_percentage as vat_percentage,
    invoice.tax_region,
    c.vat_id,
    sum(i.price_per_carat * i.cts)::numeric + invoice.additional_charges::numeric as net,
    invoice.vat as vat,
    invoice.total::numeric as gross,
    invoice.advance_payment::numeric as advanced_payment,
    invoice.total::numeric - invoice.advance_payment::numeric as remaining_balance,
    sum( p.ppc * i.cts) as buy,
    sum(i.price_per_carat * i.cts)::numeric as sale,
    (sum(i.price_per_carat * i.cts) - sum( p.ppc * i.cts))::numeric as profit,
    ((sum(i.price_per_carat * i.cts)::numeric - sum( p.ppc * i.cts))* 100/sum( p.ppc * i.cts))::numeric as margin_percentage

from invoice inner join invoice_items ii on invoice.id = ii.invoice_id
             inner join invoiced_item i on ii.items_id = i.id
             inner join (select *,to_base_currency((buy_price/ cts)::numeric, origin_currency)::numeric as ppc  from product) p on p.id = i.product_id
             inner join customer c on c.id = invoice.customer_id
where invoice.state != 'REFUNDED' group by invoice.id, c.id;