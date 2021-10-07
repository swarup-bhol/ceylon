import React from 'react'
import {LocalDatasource} from "@phantomit/datasource/lib/local/LocalDatasource";
import {Button, Card, Col, Divider, Drawer, Modal, Row, Statistic} from "antd";
import {Table} from "@phantomit/table";
import {SaveTableColumns, CustomerDataConfig, customer_overview} from "../../api/APIWrapper";

import {CloseOutlined} from "@ant-design/icons";

export const CustomerOverview = ({open,onClose,customerId})=>{


    const [{brand,company,companyId,companyMail,companyTelephone,country,taxRegion,vatId}, setData] = React.useState({})
    const [source] = React.useState(new LocalDatasource())

    return <Modal
        onCancel={onClose}
        visible={open}
        closable={true}
        title={`Customer Overview : ${customerId}`}
        centered={true}
        bodyStyle={{padding:3}}
        onOk={onClose}
        cancelButtonProps={{disabled:true}}
        width={`80vw`}
    >
        <Row >
            <Col style={{padding: 3}} span={6}>
                <Card size={"small"}><Statistic  title={`Brand`} value={brand} /></Card>
            </Col>
            <Col style={{padding: 3}} span={6}>
                <Card  size={"small"}><Statistic title={`Company`} value={company} /></Card>
            </Col>
            <Col style={{padding: 3}} span={6}>
                <Card size={"small"}><Statistic title={`Country`} value={country}/></Card>
            </Col>
            <Col style={{padding: 3}} span={6}>
                <Card size={"small"}><Statistic title={`VAT ID`} value={vatId}/></Card>
            </Col>
        </Row>
        <Row >
            <Col style={{padding: 3}} span={6}>
                <Card size={"small"}><Statistic  title={`Tax Region`} value={taxRegion} /></Card>
            </Col>
            <Col style={{padding: 3}} span={6}>
                <Card  size={"small"}><Statistic title={`Company ID`} value={companyId} /></Card>
            </Col>
            <Col style={{padding: 3}} span={6}>
                <Card size={"small"}><Statistic title={`Company Mail`} value={companyMail}/></Card>
            </Col>
            <Col style={{padding: 3}} span={6}>
                <Card size={"small"}><Statistic title={`Phone`} value={companyTelephone}/></Card>
            </Col>
        </Row>
        <Table
            source={source}
            scrollX={'max-content'}
            scrollY={'35vh'}
            fetch={async ()=>{
                const {data} =  await customer_overview(customerId)

                const [first] = data;
                setData(first);
                return data;
            }}
            save={async (row)=>{
                return row;
            }}
            delete={async (row)=>{
                return row;
            }}
            columns={async ()=>{
                const {data: {tableId,columns}} = await CustomerDataConfig();
                return columns;
            }}
            saveColumn={async (columns)=>{
                const {data} = await SaveTableColumns(columns)
                return data;
            }}
            title={`Customer Overview : ${customerId}`}
            header={false}
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
            headerButtons={[
                <Divider key={1} type={`vertical`}/>,
                <Button key={2} icon={<CloseOutlined/>} onClick={()=>{
                    onClose()
                }}/>
            ]}
        />
    </Modal>
}
