import React from 'react'
import {LocalDatasource} from "@phantomit/datasource/lib/local/LocalDatasource";
import {Button, Card, Divider, Drawer, Modal, Space, Statistic,Row,Col} from "antd";
import {Table} from "@phantomit/table";
import {SaveTableColumns,product_overview_config,product_overview} from "../../api/APIWrapper";

import {CloseOutlined} from "@ant-design/icons";

export const ProductOverview = ({open,onClose,productId, product})=>{

    const source = new LocalDatasource();

    const style = (value = 0)=>{
        console.log(value)
        return { valueStyle : {color: value <0 ? '#b01010': value > 0 ? "#029102" : "#000000"}}
    }

    return <Modal
        onCancel={onClose}
        visible={open}
        closable={true}
        title={`Product Overview : ${productId}`}
        centered={true}
        onOk={onClose}
        cancelButtonProps={{disabled:true}}
        width={`80vw`}
    >
        <Row >
            <Col style={{padding: 3}} span={8}>
                <Card size={"small"}><Statistic {...style(product.invoiced)}  title={`Invoiced Quantity`} value={product.invoiced || 0} suffix={` / ${product.qty}`}/></Card>
            </Col>
            <Col style={{padding: 3}} span={8}>
                <Card  size={"small"}><Statistic {...style(product.consigned)} title={`Consigned Quantity`} value={product.consigned || 0} suffix={` / ${product.qty}`}/></Card>
            </Col>
            <Col style={{padding: 3}} span={8}>
                <Card size={"small"}><Statistic {...style(product.qty - ((product.invoiced || 0) + (product.consigned || 0)))} title={`Available Quantity`} value={product.qty - ((product.invoiced || 0) + (product.consigned || 0))} suffix={` / ${product.qty}`}/></Card>
            </Col>
        </Row>
        <Row >
            <Col style={{padding: 3}} span={8}>
                <Card size={"small"}><Statistic {...style(product.invoicedCts)}  title={`Invoiced Cts`} precision={2} value={(product.invoicedCts || 0)} suffix={` / ${product.cts}`}/></Card>
            </Col>
            <Col style={{padding: 3}} span={8}>
                <Card size={"small"}><Statistic  {...style(product.consignedCts)}  title={`Consigned Cts`} precision={2} value={(product.consignedCts || 0)} suffix={` / ${product.cts}`}/></Card>
            </Col>
            <Col style={{padding: 3}} span={8}>
                <Card size={"small"}><Statistic {...style(product.cts - ((product.invoicedCts || 0) + (product.consignedCts || 0)))} title={`Available Cts`} precision={2} value={product.cts - ((product.invoicedCts || 0) + (product.consignedCts || 0))} suffix={` / ${product.cts}`}/></Card>
            </Col>
        </Row>
        <Row >
            <Col style={{padding: 3}} span={12}>
                <Card size={"small"}><Statistic title={`Description`} value={product.qty} suffix={` x ${product.goods} (${product.color}, ${product.shapecut}, ${product.comment})`}/></Card>
            </Col>
            <Col style={{padding: 3}} span={6}>
                <Card size={"small"}><Statistic title={`Profit`} {...style(product.sellProfit)} precision={2} value={(product.sellProfit || 0)} suffix={` €`}/></Card>
            </Col>
            <Col style={{padding: 3}} span={6}>
                <Card size={"small"}><Statistic title={`Profit %`} {...style(product.saleProfitPercentage)} precision={2} value={(product.saleProfitPercentage || 0)} suffix={` €`}/></Card>
            </Col>
        </Row>
        <Table
            source={source}
            scrollX={'max-content'}
            scrollY={'40vh'}
            fetch={async ()=>{
                const {data} =  await product_overview(productId)
                return data;
            }}
            save={async (row)=>{
                return row;
            }}
            delete={async (row)=>{
                return row;
            }}
            columns={async ()=>{
                const {data: {tableId,columns}} = await product_overview_config();
                return columns;
            }}
            saveColumn={async (columns)=>{
                const {data} = await SaveTableColumns(columns)
                return data;
            }}
            title={`Product Overview : ${productId}`}
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
                <Divider key={1} type={`vertical`}/>
            ]}

        />
    </Modal>
}
