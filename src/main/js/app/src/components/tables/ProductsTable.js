import React from 'react'
import {downloadQr, ProductData, ProductDataConfig, SaveProduct, SaveTableColumns,productUpload} from "../../api/APIWrapper";
import {Table} from "@phantomit/table";
import {Button, Drawer, Divider, Space} from "antd";
import {CloseOutlined,EyeOutlined} from "@ant-design/icons";
import {LocalDatasource} from "@phantomit/datasource/lib/local/LocalDatasource";
import {Invoice} from "../ant-d/invoice";
import moment from "moment";
import {EventType} from "@phantomit/datasource/lib/Datasource";
import {ActionType} from "@phantomit/table/lib/table/def";
import {ProductOverview} from "./ProductOverview";
import {ImportData} from "../ImportData";

const addFormatters = (columns)=>{

    return columns.map(column=>{
        const {dataIndex} = column;
        switch (dataIndex){
            case "qty":
                column.format = (row, key,defaultFormat) => {
                    if(row.consigned != null || row.invoiced != null){
                        return `${(parseInt(defaultFormat) - (row.invoiced || 0) - (row.consigned || 0)) || 0 } ( I:${row.invoiced || 0}|C:${row.consigned || 0} )`
                    }
                    return  defaultFormat
                }
            break;
            case "cts":
                column.format = (row, key,defaultFormat) => {
                    if(row.consignedCts != null || row.invoicedCts != null){
                        return  `${(parseFloat(defaultFormat) - (row.invoicedCts || 0) - (row.consignedCts || 0)).toFixed(2) || 0 } ( I:${row.invoicedCts || 0}|C:${row.consignedCts || 0} )`

                    }
                    return  defaultFormat
                }
                break;

        }

        if(column.type === "currency"){
            column.format = (row, key,defaultFormat) => {
                if(column.derivedCurrency){
                    if(row.originPrefix){
                        return `${row.originSymbol || ""} ${defaultFormat}`
                    }else{
                        return `${defaultFormat} ${row.originSymbol || ""}`
                    }
                }else{
                    if(row.basePrefix){
                        return `${row.baseSymbol || ""} ${defaultFormat}`
                    }else{
                        return `${defaultFormat} ${row.baseSymbol || ""}`
                    }
                }
            }

        }else if(column.type === "date"){
            column.format = (row, key,defaultFormat) => {
                return moment(defaultFormat).format("DD/MM/yyyy")
            }
        }

        return column;
    })
}

const source = new LocalDatasource();

export const Products = ({open,onClose})=>{

    const [opt,setOpt] = React.useState({open:false,type:1})
    const [selected,setSelected] = React.useState(false)
    const [overview,setOverview] = React.useState({open:false,id:0,data:{}})

    source.subscribe('listen',{
        key: EventType.select,
        handler: async  (type,data)=>{
            setSelected(source.selected.length > 0)
        }
    })

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
            scrollY={'calc(100vh - 134px)'}
        fetch={async ()=>{
            const {data} =  await ProductData()
            console.log(data)
            return data;
        }}
        save={async (row)=>{
            const {data} = await SaveProduct(row);
            return data;
        }}
        delete={async (row)=>{
            return row;
        }}
        columns={async ()=>{
            const {data: {tableId,columns}} = await ProductDataConfig();
            return addFormatters(columns);
        }}
        saveColumn={async (columns)=>{
            const {data} = await SaveTableColumns(columns)
            return data;
        }}
        title={`Products`}
        header={true}
        actions={{
            width:160,
            refresh:true,
            add:true,
            search:true,
            settings:true,
            export:true,
            delete:false,
            edit:true,
            select:true,
            import:false
        }}
        customActions={(row,index)=>{
            return <Button key={`custom.${index}`} icon={<EyeOutlined />} onClick={()=>{
                setOverview({open:true,id: row.id,data: row})
            }} />
        }}
        validateAction={(type, row, index)=>{
            switch (type){
                case "select":
                    const {qty =1,invoiced=0,consigned=0} = row;
                    const state =  qty > invoiced + consigned
                    return !state;
                default:
                    return false;
            }
        }}
            footer={<div style={{width:'calc(100vw - 20px)'}}>
                <Space style={{float:'right'}}>
                <Button disabled={opt.open || (!selected) } onClick={()=>{
                    source.select().then(data=>{
                        setOpt({...opt,open:true,type:1,data})
                    })

                }}>Invoice</Button>
                <Button  disabled={opt.open || (!selected) } onClick={()=>{
                    source.select().then(data=>{
                        setOpt({...opt,open:true,type:2,data})
                    })
                }}>Consign</Button>
                <Button  disabled={opt.open || (!selected) } onClick={()=>{
                    source.select().then(data=>{
                        setOpt({...opt,open:true,type:3,data})
                    })
                }}>Offer</Button>
                <Button  disabled={opt.open || (!selected) } onClick={()=>{
                    source.select().then(data=>{
                    downloadQr(data.map(({id})=>`${id}`).join(','))
                    })
                }}>QR</Button>
                    <ImportData upload={async (data)=>{                        
                        return await productUpload(data)
                    }}/>
                </Space>
            </div>}
        headerButtons={[
            <Divider key={1} type={`vertical`}/>,
            <Button key={2} icon={<CloseOutlined/>} onClick={()=>{
                onClose()
            }}/>
        ]}

    />
        {opt.open && <Invoice type={opt.type} data={opt.data.map(({qty,cts,minPricePerCarat,...args})=>({
            product:{qty,cts,minPricePerCarat,...args},
            qty,
            cts,
            pricePerCarat:minPricePerCarat
        }))} visible={open} onClose={() => {
            source.selected = [];
            source.trigger(EventType.custom,{action:ActionType.select,value:{}})
            setOpt({...opt,open: false})
        }
        }/>}

        {
            overview.open && <ProductOverview
                onClose={()=>{
                    setOverview({open: false, id: 0, data:{}})
                }}
                product={overview.data}
                productId={overview.id}
                open={overview.open}/>
        }
    </Drawer>
}
