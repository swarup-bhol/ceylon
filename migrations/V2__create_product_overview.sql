drop table if exists product_overview;
create or replace view product_overview as
select
    row_number() over (),
    items.document_id,
    in_i.cts,
    round(in_i.price_per_carat::numeric,2) as price_per_carat,
    round(in_i.cts::numeric * in_i.price_per_carat::numeric, 2) as price,
    in_i.qty,
    in_i.type,
    items.state,
    p.id as product_id,
    concat_ws(' ',p.goods,'(' ,concat_ws(',',p.color,p.shapecut,p.comment), ')') as description,
    cm.company
from (select i.id as document_id, ii.items_id, i.customer_id, i.state from (select * from invoice ) as i
                                                                inner join invoice_items ii on i.id = ii.invoice_id
      union all
      select  c.id as document_id, ci.items_id, c.customer_id, c.state from (select * from consignment) as c
                                                                inner join consignment_items ci on c.id = ci.consignment_id) as items inner join invoiced_item in_i
                                                                                                                                                 on items.items_id = in_i.id
                                                                                                                                      inner join product p on in_i.product_id = p.id
                                                                                                                                      inner join customer cm on cm.id = items.customer_id;
drop table if exists product_state;
create or replace  view product_state as
select ii.product_id, i.state,ii.qty,i.due_date,ii.type as item_type from invoice_items inner join invoiced_item ii on ii.id = invoice_items.items_id inner join invoice i on i.id = invoice_items.invoice_id where i.state != 'REFUNDED'
union all
select i.product_id,c.state, i.qty,c.due_date, i.type as item_type from consignment_items inner join invoiced_item i on i.id = consignment_items.items_id inner join consignment c on c.id = consignment_items.consignment_id where c.state !='RETURNED';


drop table if exists product_summary;
create or replace  view  product_summary as
select product_id,SUM(qty) as qty, string_agg(state,'|' order by product_id) as states, string_agg(due_date,'|'  order by product_id) as due_dates, item_type from product_state group by product_id,item_type;





