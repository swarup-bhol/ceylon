

drop view product_summary;
drop view product_state;

create or replace  view product_state as
select ii.product_id, i.state,ii.qty,ii.cts,i.due_date,ii.type as item_type from invoice_items inner join invoiced_item ii on ii.id = invoice_items.items_id inner join invoice i on i.id = invoice_items.invoice_id where i.state != 'REFUNDED'
union all
select i.product_id,c.state, i.qty,i.cts,c.due_date, i.type as item_type from consignment_items inner join invoiced_item i on i.id = consignment_items.items_id inner join consignment c on c.id = consignment_items.consignment_id where c.state !='RETURNED';

create or replace  view  product_summary as
select product_id,SUM(qty) as qty, SUM(cts) as cts, string_agg(state,'|' order by product_id) as states, string_agg(due_date,'|'  order by product_id) as due_dates, item_type from product_state group by product_id,item_type;


