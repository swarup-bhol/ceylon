import React from "react";
import {LocalDatasource} from "@phantomit/datasource/lib/local/LocalDatasource";
import {Button, Divider, Drawer, Modal} from "antd";
import {Table} from "@phantomit/table";
import {
    SaveOffer,
    Offers,
    OffersConfig,
    SaveTableColumns
} from "../../api/APIWrapper";
import {CloseOutlined, DownloadOutlined} from "@ant-design/icons";
import {Invoice,InvoiceType} from "../ant-d/invoice";
import {EventType, Row} from "@phantomit/datasource/lib/Datasource";
import {ActionType} from "@phantomit/table/lib/table/def";


export const OfferTable = ({open, onClose})=>{

    const [source] = React.useState(new LocalDatasource());

    source.addFormatter('customer', {
        key: 'customer',
        value: (row, key, rowIndex, columnIndex) => {
            return 'Hello World'
        }
    });

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
                const {data} =  await Offers()
                return data;
            }}
            save={async (row)=>{
                const {data} = await SaveOffer(row)
                return data
            }}
            delete={async (row)=>{
                return row;
            }}
            columns={async ()=>{
                const {data: {columns}} = await OffersConfig();
                return columns;
            }}
            saveColumn={async (columns)=>{
                const {data} = await SaveTableColumns(columns)
                return data;
            }}
            title={`Offers`}
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
                    <Button key={`view`} onClick={()=>{
                        setRow(row)
                    }}>View</Button>,

                ]
            }}
            headerButtons={[
                <Divider key={1} type={`vertical`}/>,
                <Button key={2} icon={<CloseOutlined/>} onClick={()=>{
                    onClose()
                }}/>
            ]}

        />
        {
            row && <Modal
                closable={true}
                onCancel={setRow(undefined)}
                centered={true}
                title={'Offer #'+ `${row.id }`.padStart(6,'0')}
            >

            </Modal>
        }
    </Drawer>
}
