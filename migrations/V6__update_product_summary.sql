

drop view product_summary;
drop view product_state;

drop view sales;

create or replace  view product_state as
select ii.product_id, i.state,ii.qty,ii.cts,i.due_date,ii.price_per_carat,ii.type as item_type from invoice_items inner join invoiced_item ii on ii.id = invoice_items.items_id inner join invoice i on i.id = invoice_items.invoice_id where i.state != 'REFUNDED'
union all
select i.product_id,c.state, i.qty,i.cts,c.due_date,i.price_per_carat, i.type as item_type from consignment_items inner join invoiced_item i on i.id = consignment_items.items_id inner join consignment c on c.id = consignment_items.consignment_id where c.state ='RETURNED' or c.state = 'OVERDUE';

create or replace  view  product_summary as
select product_id,SUM(qty) as qty,SUM(price_per_carat * cts) as price, SUM(cts) as cts, string_agg(state,'|' order by product_id) as states, string_agg(due_date,'|'  order by product_id) as due_dates, item_type from product_state group by product_id,item_type;


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
    invoice.pos_lv1,
    invoice.pos_lv2,
    invoice.rtb,
    invoice.fair as fair_trade,
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