import React from "react";
import {LocalDatasource} from "@phantomit/datasource/lib/local/LocalDatasource";
import {Button, Divider, Drawer} from "antd";
import {Table} from "@phantomit/table";
import {
    SaveConsignment,
    Consignment,
    ConsignmentConfig,
    downloadInvoice,
    SaveTableColumns
} from "../../api/APIWrapper";
import {CloseOutlined, DownloadOutlined} from "@ant-design/icons";
import {Invoice,InvoiceType} from "../ant-d/invoice";
import {EventType} from "@phantomit/datasource/lib/Datasource";
import {ActionType} from "@phantomit/table/lib/table/def";


export const ConsignmentTable = ({open, onClose})=>{

    const [source] = React.useState(new LocalDatasource());
    const [row,setRow] = React.useState();
    return <Drawer
        visible={open}
        width={`100vw`}
        closable={false}
        onClose={onClose}
        bodyStyle={{padding: 0}}
    >
        <Table
            source={source}
            scrollX={'max-content'}
            scrollY={'calc(100vh - 84px)'}
            fetch={async ()=>{
                const {data} =  await Consignment()
                return data;
            }}
            save={async (row)=>{
                    const {data} = await SaveConsignment(row)
                    return data
            }}
            delete={async (row)=>{
                return row;
            }}
            columns={async ()=>{
                const {data: {columns}} = await ConsignmentConfig();
                return columns;
            }}
            saveColumn={async (columns)=>{
                const {data} = await SaveTableColumns(columns)
                return data;
            }}
            title={`Consignments`}
            header={true}
            actions={{
                width:160,
                refresh:true,
                add:false,
                search:true,
                settings:true,
                export:true,
                delete:false,
                edit:false,
                select:false,
                import:false
            }}

            customActions={(row,index)=>{
                return [
                    <Button key={`download`} icon={<DownloadOutlined />} onClick={()=>{
                        downloadInvoice(`consignment/${row.id}`)
                    }}/>,

                    <Button className="ant-btn-danger"  disabled={['RETURNED','PARTIALLY_INVOICED', 'INVOICED'].includes(row.state)} key={`return`} onClick={async ()=>{
                        await source.trigger(EventType.custom, {
                            action: ActionType.update,
                            value: {...row,state: 'RETURNED'}
                        });

                    }}>Return</Button>,
                    <Button disabled={['RETURNED','PARTIALLY_INVOICED', 'INVOICED'].includes(row.state)} key={`complete`} onClick={()=>{
                        console.log(row)
                        setRow(row)
                    }}>Invoice</Button>,

                ]
            }}
            headerButtons={[
                <Divider key={1} type={`vertical`}/>,
                <Button key={2} icon={<CloseOutlined/>} onClick={()=>{
                    onClose()
                }}/>
            ]}

        />
        {row && <Invoice
            type={InvoiceType.Invoice}
            data={row.items || []}
            visible={true}
            customer={row.customer}
            fixedCustomer
            consignmentId={row.id}
            onFinalize={async (invoice) => {
                if(invoice){
                    const state = invoice.items.length < row.items.length ? 'PARTIALLY_INVOICED' : 'INVOICED'
                    await source.trigger(EventType.custom, {
                        action: ActionType.update,
                        value: {...row,state}
                    })
                }

            }
            }
            onClose={()=>{
                setRow(undefined)
            }}
        />}
    </Drawer>
}
