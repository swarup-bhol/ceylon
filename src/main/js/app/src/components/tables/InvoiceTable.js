import React from 'react'
import PropTypes from 'prop-types'
import {CloseOutlined,DownloadOutlined} from "@ant-design/icons";
import {
    CompleteInvoice, downloadInvoice,
    InvoiceConfig,
    Invoices,
    RefundInvoice, SaveTableColumns,
} from "../../api/APIWrapper";
import {LocalDatasource} from "@phantomit/datasource/lib/local/LocalDatasource";
import {Divider, Drawer, Button, Modal, Space,DatePicker,Input,Typography} from "antd";
import {Table} from "@phantomit/table";
import {EventType} from "@phantomit/datasource/lib/Datasource";
import {ActionType} from "@phantomit/table/lib/table/def";
import {InvoiceType} from "../ant-d/invoice";
import moment from "moment";

export const InvoiceCompleter = ({open, row:{id} = {}, onClose, onOk})=>{

    const [data,setData] = React.useState({
        completedOn: moment(),
        paymentId: ''
    })

    return <React.Fragment>
        <Modal
            centered
            visible={open}
            title={`Complete Invoice #${id}`}
            onOk={()=>{
                const {completedOn, paymentId = 'N/A'} = data;
                onOk({completedOn: completedOn.format('YYYY-MM-DD'), paymentId})
            }}
            onCancel={onClose}
            okText={`Complete`}
        >
            <Space size={12}>
                <Space size={0}>
                    <Typography.Text>{`Transfer Date`}</Typography.Text>
                    <DatePicker value={data.completedOn} onChange={(date)=>setData({...data, completedOn: date})}/>
                </Space>
                <Space size={0}>
                    <Typography.Text>{`Payment Id`}</Typography.Text>
                    <Input value={data.paymentId} onChange={(e)=>{setData({...data, paymentId: e.target.value})}}/>
                </Space>
            </Space>
        </Modal>
    </React.Fragment>
}

export const InvoiceTable = ({open, onClose})=>{

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
                const {data} =  await Invoices()
                return data;
            }}
            save={async (row)=>{
                if(row.state === 'REFUNDED'){
                    const {data} = await RefundInvoice(row)
                    return data
                }else if(row.state === 'COMPLETE'){
                    const {data} = await CompleteInvoice(row)
                    return data
                }
            }}
            delete={async (row)=>{
                return row;
            }}
            columns={async ()=>{
                const {data: {tableId,columns}} = await InvoiceConfig();
                return columns;
            }}
            saveColumn={async (columns)=>{
                const {data} = await SaveTableColumns(columns)
                return data;
            }}
            title={`Invoices`}
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
                        downloadInvoice(`invoice/${row.id}`)
                    }}/>,
                    <Button disabled={row.state === 'COMPLETE'} key={`complete`} onClick={()=>{
                        setRow(row)
                    }}>Complete</Button>,
                    <Button disabled={row.state === 'REFUNDED'}  key={`refund`} onClick={()=>{
                        source.trigger(EventType.custom, {
                            action: ActionType.update,
                            value: {...row, state: 'REFUNDED'}
                        })
                    }}>Refund</Button>
                    ]
            }}
            headerButtons={[
                <Divider key={1} type={`vertical`}/>,
                <Button key={2} icon={<CloseOutlined/>} onClick={()=>{
                    onClose()
                }}/>
            ]}

        />
        <InvoiceCompleter
            onOk={({completedOn, paymentId})=>{
                source.trigger(EventType.custom, {
                    action: ActionType.update,
                    value: {...row, state: 'COMPLETE',completedOn,paymentId}
                }).then(()=>{
                    setRow(undefined);
                })
                }}
            onClose={()=>{
                setRow(undefined);
            }}
            open={row != null}
            row={row}
        />
    </Drawer>
}


InvoiceTable.propTypes = {
    open:PropTypes.bool.isRequired,
    onClose:PropTypes.func.isRequired
};
