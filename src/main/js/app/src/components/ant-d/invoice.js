import React from 'react'
import {Table, Modal, Button, InputNumber, Space, Col, Tooltip, Checkbox, Card, Divider} from "antd";
import Text from "antd/es/typography/Text";
import {CheckOutlined, CloseOutlined, DeleteOutlined, EditOutlined} from "@ant-design/icons";
import {DatePicker,Select,message, AutoComplete} from "antd";
import moment from "moment";
import {CustomerData,SaveInvoice,SaveConsignment,downloadInvoice, SaveOffer} from "../../api/APIWrapper";
const posEnums = {
    Visit:['Visit'],
    Network:['Trader', 'Existing Customer' ],
    Online:[ 'Website (Email,Inquiry und Telefon)','Facebook','Instagram','Newsletter','Auction','Webshop'],
    Retail:['Retail Shop'],
    Recommendation:['Business', 'Private'],
    'Fair Show':['Fair Show -Gemworld', 'Fair Show - Inhorgenta'],
    Event:[],
    Others:[],
    Internal:['Internal']

}

const rtbEnums = ['Request',
    'Stock',
    'Consignment',
    'Bespoke',
    'Collection',
    'Bespoke Jewelery',
    'Collecting',
    'Value Invest',
    'Internal',
    'Others'
]


export const AdditionalInfo = ({posLv1,posLv2,rtb,fair,onChange,isFinalized=false})=>{

    const [data,setData] = React.useState({posLv1,posLv2,rtb,fair})

    const update = ({posLv1,posLv2,rtb,fair})=>{
        if(onChange)
            onChange({posLv1,posLv2,rtb,fair})
        setData({posLv1,posLv2,rtb,fair})
    }

    return <React.Fragment>
        <Space>
            <Card size={"small"}>
                <Space>
                    <Text>Point of Sale : </Text>
                    <AutoComplete
                        disabled={isFinalized}
                        value={data.posLv1}
                        onChange={(e)=>update({...data,posLv1: e})}
                        options={Object.keys(posEnums).map(o=>({label:o, value: o}))}
                        style={{ width: 200 }}
                        placeholder={`Level 1`}
                        backfill
                        filterOption
                    />
                    <AutoComplete
                        disabled={isFinalized}
                        value={data.posLv2}
                        onChange={(e)=>update({...data,posLv2: e})}
                        options={(posEnums[data.posLv1] || []).map(o=>({label:o, value: o}))}
                        style={{ width: 200 }}
                        placeholder={`Level 2`}
                        backfill
                        filterOption
                    />
                </Space>
            </Card>
            <Card  size={"small"}>
                <Space>
                    <Text>Reason to Buy</Text>
                    <AutoComplete
                        disabled={isFinalized}
                        value={data.rtb}
                        onChange={(e)=>update({...data,rtb: e})}
                        options={rtbEnums.map(o=>({label:o, value: o}))}
                        style={{ width: 200 }}
                        placeholder={`Reason to Buy`}
                        backfill
                        filterOption
                    />
                </Space>
            </Card>
            <Card  size={"small"} style={{height: 58}}>
                <Checkbox value={data.fair} disabled={isFinalized} onChange={(e)=>update({...data, fair: e.target.checked})}>
                    <Text>Fair</Text>
                </Checkbox>
            </Card>
        </Space>

    </React.Fragment>
}


export const Customer = ({customerId,disabled,onChange})=>{

    const [opt,setOpt] = React.useState({data:[],loading:true})

    const customers = async ()=>{
        const {data} = await CustomerData();
        setOpt({data,loading: false})
    }

    React.useEffect(()=>{
       if(opt.loading)
            customers();
    },[])


    const items = opt.data.map(({
                                id,
                                company,
                                name,
                                firstName,
                            },index)=>{
        const displayText = `${company}/${firstName} ${name}`;
        return <Select.Option key={index} value={id}>
                {displayText}
        </Select.Option>
    })

    return <Select
        loading={opt.loading}
        disabled={disabled}
        defaultValue={customerId}
        value={customerId}
        onChange={(value)=>{
            const {id,taxRegion} = opt.data.find(({id})=>id === value)
            onChange({id,taxRegion})
        }}
        showSearch
        style={{ width: 200 }}
        placeholder="Select a Customer"
        optionFilterProp="children"
        filterOption={(input, option) =>
            option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
        }
    >
        {items}
    </Select>
}


export const InvoiceType = Object.freeze({
    Invoice: 1,
    Consign: 2,
    Offer: 3
})

export const Header = ({title,customerId,type=InvoiceType.Invoice,taxRegion,from,to,isFinalized,fixedCustomer=false,onCustomer,onDate,onTaxRegionChange})=>{


    const fromDate = from ? moment(from) : moment()
    const toDate = to ? moment(to) : null
    console.log(`header customer id = ${customerId} taxRegion=${taxRegion}`)
    return <Space direction={"vertical"}>
        <Space size={80}>
            <Col>
                {title}
            </Col>
            <Col>
                <Customer disabled={isFinalized  || fixedCustomer} customerId={customerId} onChange={onCustomer}/>
            </Col>
            {type === InvoiceType.Invoice && <Col>
                <Select
                    key={taxRegion}
                    disabled={isFinalized}
                    defaultValue={taxRegion}
                    value={taxRegion}
                    placeholder={`Tax Region`}
                    onChange={(value) => {
                        onTaxRegionChange(value)
                    }}
                >
                    <Select.Option value={"41"}>41</Select.Option>
                    <Select.Option value={"43"}>43</Select.Option>
                    <Select.Option value={"81"}>81</Select.Option>
                </Select>
            </Col>}
            {<Col>
                <DatePicker.RangePicker key={to} defaultValue={[fromDate, toDate]} disabled={[true, isFinalized]}
                                        onChange={(dates, str) => {
                                            onDate(str)
                                        }}/>
            </Col>}
        </Space>
    </Space>
}



export const Invoice = (props)=>{

    const {type = InvoiceType.Invoice,
        customer:{id, taxRegion} = {},
        fixedCustomer = false,
        consignmentId,
        onFinalize = async (data)=>{}
    } = props;

    console.log(`id = ${id} taxRegion = ${taxRegion}`);
    const [invoice, setInvoice] = React.useState({
        loading:false,
        isFinalized: props.isFinalized,
        id:props.invoiceId,
        additionalCharges:0,
        vat:0,
        vatPercentage:0,
        advancePayment:0,
        customer:{id},
        taxRegion:taxRegion,
        posLv1: undefined,
        posLv2: undefined,
        rtb:undefined,
        fair:false,
        consignmentId,
        dueDate: null,
        date: props.date || moment().format("YYYY-MM-DD"),
        items:props.data.map(({
                                  product: {id,goods,color, shapecut,comment,lotBatchSetPair=""} = {},
                                  qty,
                                  cts,
                                  pricePerCarat
                              })=>({
            description:`${goods} (${color}, ${shapecut}, ${comment})`,
            id:id,
            isBatch:  lotBatchSetPair && lotBatchSetPair.toLowerCase() === "batch",
            qty:qty,
            cts:cts,
            pricePerCarat:pricePerCarat,
            price:cts * pricePerCarat
        }))
    });



    const action = async ({items,...args})=>{
        const mapped = items.map(({id, pricePerCarat,...o})=>({
            ...o,
            pricePerCarat: pricePerCarat,
            product:{id:id}
        }))
        const data = {...args,items:mapped}

        const {data:saved} = type === InvoiceType.Invoice ? await SaveInvoice(data): type === InvoiceType.Offer ? await SaveOffer(data):  await SaveConsignment(data)
        await onFinalize(saved);
        setInvoice({...data,...saved,loading: false,isFinalized: true})
    }

    const download = async (id)=>{
        await downloadInvoice(type === InvoiceType.Invoice ? `invoice/${id}` : `consignment/${id}` )
        setInvoice({...invoice,loading: false})
    }

    const columns = [
        {
            title:"",
            dataIndex:"",
            width:45,
            align: "center",
            render: (value, row, index)=>{
                return <Button disabled={invoice.isFinalized}  style={{marginLeft:5}} icon={<DeleteOutlined />} onClick={(e)=>{
                    let {items = []} = invoice;
                    items.splice(index,1);
                    setInvoice({...invoice,items: [...items]})
                }}/>
            }
        },
        {
            title: "Description",
            align: "center",
            dataIndex: "description"
        },
        {
            title: "No",
            width:100,
            align: "center",
            dataIndex: "id"
        },
        {
            title: "Qty",
            align: "center",
            width:100,
            dataIndex: "qty",
            render: (value, row, index)=>{
                return invoice.isFinalized || (!invoice.items[index].isBatch) ? invoice.items[index].qty :<InputNumber value={invoice.items[index].qty} key={`${index}.qty`} precision={0} onChange={(value)=>{
                    let {items = []} = invoice;
                    items[index].qty = value
                    setInvoice({...invoice,items: [...items]})
                }}/>
            }
        },
        {
            title: "Cts",
            align: "center",
            width:100,
            dataIndex: "cts",
            render: (value, row, index)=>{
                return invoice.isFinalized ? invoice.items[index].cts :<InputNumber value={invoice.items[index].cts} key={`${index}.cts`} precision={2} onChange={(value)=>{
                    let {items = []} = invoice;
                    items[index].cts = value
                    const sum = items.reduce((a,b)=>a + (b.cts * b.pricePerCarat), 0)
                    setInvoice({
                        ...invoice,
                        items: [...items],
                        vat: ((sum + value) * invoice.vatPercentage / 100),

                    })
                }}/>
            }
        },
        {
            title: "€/Ct",
            align: "center",
            width:100,
            dataIndex: "pricePerCarat",
            render: (value, row, index)=>{
                return invoice.isFinalized ? `${invoice.items[index].pricePerCarat.toFixed(2)} €` :<InputNumber value={invoice.items[index].pricePerCarat} key={`${index}.pricePerCarat`} precision={0} onChange={(value)=>{
                    let {items = []} = invoice;
                    items[index].pricePerCarat = value
                    const sum = items.reduce((a,b)=>a + (b.cts * b.pricePerCarat), 0)
                    setInvoice({
                        ...invoice,
                        items: [...items],
                        vat: ((sum + value) * invoice.vatPercentage / 100)
                    })
                }}/>
            }
        },
        {
            title: "Price (€)",
            align: "center",
            width:150,
            dataIndex: "price",
            render: (value, row, index)=>{
                const {cts,pricePerCarat} = invoice.items[index];
                return invoice.isFinalized ? `${(cts * pricePerCarat).toFixed(2)} €`:<InputNumber value={cts * pricePerCarat} key={`${index}.price`} precision={0} onChange={(value)=>{
                    let {items = []} = invoice;
                    const {cts} = items[index]
                    const calculated = Math.ceil(value * 100 / cts)/100
                    items[index].pricePerCarat = calculated
                    const sum = items.reduce((a,b)=>a + (b.cts * b.pricePerCarat), 0)
                    setInvoice({
                        ...invoice,
                        items: [...items],
                        vat: ((sum + value) * invoice.vatPercentage / 100)
                    })
                }}/>
            }
        }

    ]

    const total = invoice.items.reduce((a,b)=>a + (b.cts * b.pricePerCarat), 0)
    console.log(invoice)
    return <React.Fragment>
            <Modal
                visible={props.visible}
                closable={ !invoice.loading}
                title={
                    <Header
                        title={`${type === InvoiceType.Invoice ? "Invoice" : type === InvoiceType.Offer ? "Offer" :  "Consignment"} ${invoice.id ? ": #" + `${invoice.id}`.padStart(6,'0') : ""}`}
                        customerId={invoice.customer.id}
                        from={invoice.date}
                        type={type}
                        to={invoice.dueDate}
                        isFinalized={invoice.isFinalized}
                        fixedCustomer={fixedCustomer}
                        taxRegion={invoice.taxRegion}
                        onCustomer={({id,taxRegion})=>{
                            setInvoice({...invoice,customer: {id},taxRegion: taxRegion})
                        }}
                        onDate={([from,to])=>{
                            setInvoice({...invoice,date: from,dueDate: to})
                        }}
                        onTaxRegionChange={(value)=>{setInvoice({...invoice,taxRegion: value})}}
                    />
                }
                centered
                width={`80vw`}
                onCancel={()=>{
                    props.onClose(invoice)
                }}
                footer={[
                    <Button disabled={invoice.loading} hidden={invoice.isFinalized} key={10} onClick={()=>props.onClose()}>Cancel</Button>,
                    <Button disabled={invoice.loading} hidden={!invoice.isFinalized} key={0} onClick={()=>props.onClose(invoice)}>Finish</Button>,
                    <Button  disabled={invoice.loading} hidden={invoice.isFinalized} type={"primary"} key={1} onClick={()=> {
                        if(invoice.customer.id == null)
                            message.warn("Please select customer",1)
                        else {
                            setInvoice({...invoice, loading: true})
                            action(invoice)
                        }
                    }}>Finalize</Button>,
                    <Button  disabled={invoice.loading} hidden={!invoice.isFinalized || type === InvoiceType.Offer} style={{float: 'left'}} type="primary" key={2} onClick={()=>{
                        setInvoice({...invoice,loading: true})
                        download(invoice.id)
                    }}>Download Pdf</Button>,
                    <Button className="ant-btn-danger" disabled={invoice.loading} hidden={!invoice.isFinalized || type !== InvoiceType.Offer} style={{float: 'left'}} type="primary" key={3} onClick={()=>{

                    }}>Email</Button>,
                ]}
            >
                {type === InvoiceType.Invoice && <AdditionalInfo
                    posLv1={invoice.posLv1}
                    posLv2={invoice.posLv2}
                    rtb={invoice.rtb}
                    fair={invoice.fair}
                    isFinalized={invoice.isFinalized}
                    onChange={(e) => setInvoice({...invoice, ...e})}
                />}
                <Divider style={{margin: 5}}/>
                <Table
                    size={`small`}
                    columns={columns}
                    loading={invoice.loading}
                    dataSource={invoice.items}
                    pagination={false}
                    rowKey={`id`}
                    scroll={{y:'55vh'}}
                    summary={()=>{
                        return <React.Fragment>
                            <Table.Summary.Row>
                                <Table.Summary.Cell  colSpan={6}>SubTotal</Table.Summary.Cell>
                                <Table.Summary.Cell className="ant-message-notice">{total.toFixed(2)} €</Table.Summary.Cell>
                            </Table.Summary.Row>
                            {type === InvoiceType.Invoice && <React.Fragment>
                                <Table.Summary.Row>
                                    <Table.Summary.Cell colSpan={6}>Processing and Shipping Cost</Table.Summary.Cell>
                                    <Table.Summary.Cell className="ant-message-notice">
                                        {invoice.isFinalized ? `${invoice.additionalCharges} €` :
                                            <InputNumber key={`summary.psc`} precision={2}
                                                         value={invoice.additionalCharges}
                                                         onChange={value => setInvoice({
                                                             ...invoice,
                                                             additionalCharges: value,
                                                             vat: ((total + value) * invoice.vatPercentage / 100),

                                                         })}/>}
                                    </Table.Summary.Cell>
                                </Table.Summary.Row>
                                <Table.Summary.Row>
                                    <Table.Summary.Cell colSpan={6}>Subtotal Without VAT</Table.Summary.Cell>
                                    <Table.Summary.Cell className="ant-message-notice">
                                        {(total + invoice.additionalCharges).toFixed(2)} €
                                    </Table.Summary.Cell>
                                </Table.Summary.Row>
                                <Table.Summary.Row>
                                    <Table.Summary.Cell colSpan={5}>VAT</Table.Summary.Cell>
                                    <Table.Summary.Cell className="ant-message-notice">
                                        {invoice.isFinalized ? `${invoice.vatPercentage} %` :
                                            <InputNumber key={`summary.vat`} suffix={"%"} precision={2}
                                                         value={invoice.vatPercentage}
                                                         onChange={value => setInvoice({
                                                             ...invoice,
                                                             vat: ((total + invoice.additionalCharges) * value / 100),
                                                             vatPercentage: value
                                                         })}/>}
                                    </Table.Summary.Cell>
                                    <Table.Summary.Cell className="ant-message-notice">
                                        { ((total + invoice.additionalCharges) * invoice.vatPercentage / 100).toFixed(2)} €
                                    </Table.Summary.Cell>
                                </Table.Summary.Row>
                                <Table.Summary.Row>
                                    <Table.Summary.Cell colSpan={6}><Text>Total</Text></Table.Summary.Cell>
                                    <Table.Summary.Cell className="ant-message-notice">
                                        {((total + invoice.additionalCharges) * (100 + invoice.vatPercentage) / 100).toFixed(2)} €
                                    </Table.Summary.Cell>
                                </Table.Summary.Row>
                                <Table.Summary.Row>
                                    <Table.Summary.Cell colSpan={6}>Advanced Payment</Table.Summary.Cell>
                                    <Table.Summary.Cell className="ant-message-notice">
                                        {invoice.isFinalized ? `${invoice.advancePayment} €` :
                                            <InputNumber key={`summary.ap`} precision={2} value={invoice.advancePayment}
                                                         onChange={value => setInvoice({
                                                             ...invoice,
                                                             advancePayment: value
                                                         })}/>}
                                    </Table.Summary.Cell>
                                </Table.Summary.Row>
                                <Table.Summary.Row>
                                    <Table.Summary.Cell colSpan={6}>Remaining Balance</Table.Summary.Cell>
                                    <Table.Summary.Cell className="ant-message-notice">
                                        {(((total + invoice.additionalCharges) * (100 + invoice.vatPercentage) / 100) - invoice.advancePayment).toFixed(2)} €
                                    </Table.Summary.Cell>
                                </Table.Summary.Row>
                            </React.Fragment>}
                        </React.Fragment>
                    }}
                />
            </Modal>
    </React.Fragment>
}



export default Invoice
