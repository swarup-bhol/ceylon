import React from "react";
import { Tile } from "./Tile";
import API from "../api/APIWrapper";
import Img from "react-image";
import { InvoiceTable } from "./tables/InvoiceTable";
import { ConsignmentTable } from "./tables/ConsignmentTable";
import { Products as ProductsTable } from "./tables/ProductsTable";
import { DatePicker, Typography } from "antd";
import { SimpleTable } from "./tables/SimpleTable";
import { Space, Row, Col, Divider, Tooltip } from "antd";
import { SettingOutlined } from "@ant-design/icons";
import { CustomerTable } from "./tables/CustomerTable";
import { OfferTable } from "./tables/OfferTable";
import { signInRequired } from "../auth/auth";
import { AmplifySignOut } from "@aws-amplify/ui-react";
import { useHistory } from "react-router-dom";

const details = (state) => {
  console.log(state);
  // if(state.tabName === "Invoices")
  //     return <InvoiceTable open={state.tabName !== ""}/>
  // else if(state.tabName === "Consignment")
  //     return <ConsignmentTable onClose={()=>{setState({tabName:""})}} open={state.tabName !== ""}/>
  // else if (state.tabName === "Product")
  //     return <ProductsTable open={state.tabName !== ""} onClose={()=>{
  //         setState({tabName:""})
  //     }}/>
  // else if (state.tabName === "Offers")
  //     return <OfferTable open={state.tabName !== ""} onClose={()=>{
  //         setState({tabName:""})
  //     }}/>
  // else if(state.tabName === "Customer"){
  //     return <CustomerTable
  //         onClose={()=>{setState({tabName:""})}}
  //         open={state.tabName !== ""}
  //         title={state.tabName}
  //         fetch={API[state.tabName].data}
  //         save={API[state.tabName].save}
  //         config={API[state.tabName].config}
  //     />
  // }else if(state.tabName === "Sales"){
  //     return <SimpleTable
  //         onClose={()=>{setState({tabName:""})}}
  //         open={state.tabName !== ""}
  //         title={state.tabName}
  //         fetch={API[state.tabName].data}
  //         save={API[state.tabName].save}
  //         config={API[state.tabName].config}
  //         actions={{
  //             refresh:true,
  //             add:false,
  //             search:true,
  //             settings:true,
  //             export:true,
  //             delete:false,
  //             edit:false,
  //             select:false,
  //             import:false
  //         }}
  //     />
  // }else if(state.tabName !== ""){
  //     return <SimpleTable
  //         onClose={()=>{setState({tabName:""})}}
  //         open={state.tabName !== ""}
  //         title={state.tabName}
  //         fetch={API[state.tabName].data}
  //         save={API[state.tabName].save}
  //         config={API[state.tabName].config}
  //     />
  // }
};

const Dashboard = (props) => {
  const [state, setState] = React.useState({ tabName: "" });
  const { title } = props;
  const history = useHistory();
  return (
    <React.Fragment>
      <div style={{ display: "flex", flexFlow: "column", height: "100%" }}>
        <div style={{ flex: "0 1 auto" }}>
          <Row style={{ padding: 5 }}>
            <Col span={21} style={{ paddingLeft: 20 }}>
              <Typography.Title level={3}>{title}</Typography.Title>
            </Col>
            <Col span={3} style={{ textAlign: "right" }}>
              <AmplifySignOut
                handleAuthStateChange={(e) => {
                  if (e === "signedout") {
                    history.push("/signIn");
                  }
                }}
              />
            </Col>
          </Row>

          <Divider style={{ margin: 5 }} />
        </div>
        <Space direction={"vertical"} style={{ width: "100%" }}>
          <Row>
            <Col style={{ padding: 12 }} span={6}>
              <Tile
                title="Product"
                tileIcon={require("../images/Orders.png")}
                focusFieldName="Products Table"
                focusFieldValue="12/22"
                summary="See all product details"
                onClick={() => {
                  setState({ tabName: "Product" });
                }}
              />
            </Col>
            <Col style={{ padding: 12 }} span={6}>
              <Tile
                title="Customer"
                tileIcon={require("../images/Clients.png")}
                focusFieldName="Customer Table"
                focusFieldValue="1255"
                summary="See all customer details"
                onClick={() => {
                  setState({ tabName: "Customer" });
                }}
              />
            </Col>
            <Col style={{ padding: 12 }} span={6}>
              <Tile
                title="Currency Details"
                tileIcon={require("../images/Sales.png")}
                focusFieldName="Currency Table"
                focusFieldValue=""
                summary="System Currency parameters"
                onClick={() => {
                  setState({ tabName: "Currency" });
                }}
              />
            </Col>
            <Col style={{ padding: 12 }} span={6}>
              <Tile
                title="Consignments"
                tileIcon={require("../images/Sales.png")}
                focusFieldName="Consignment Table"
                focusFieldValue="123"
                summary="Consignment issued"
                onClick={() => {
                  setState({ tabName: "Consignment" });
                }}
              />
            </Col>
          </Row>
          <Row>
            <Col style={{ padding: 12 }} span={6}>
              <Tile
                title="Exchange Rates"
                tileIcon={require("../images/Sales.png")}
                focusFieldName="Exchange Rates Table"
                focusFieldValue=""
                summary="System exchange rate parameters"
                onClick={() => {
                  setState({ tabName: "ExchangeRates" });
                }}
              />
            </Col>
            <Col style={{ padding: 12 }} span={6}>
              <Tile
                title="Invoices"
                tileIcon={require("../images/Sales.png")}
                focusFieldName="Invoice Table"
                focusFieldValue="123"
                summary="Invoices Overview"
                onClick={() => {
                  setState({ tabName: "Invoices" });
                }}
              />
            </Col>
            <Col style={{ padding: 12 }} span={6}>
              <Tile
                title="Sales"
                tileIcon={require("../images/Sales.png")}
                focusFieldName="Sales Table"
                focusFieldValue="123"
                summary="Sales Overview"
                onClick={() => {
                  setState({ tabName: "Sales" });
                }}
              />
            </Col>
            <Col style={{ padding: 12 }} span={6}>
              <Tile
                title="Offers"
                tileIcon={require("../images/Sales.png")}
                focusFieldName="Offers Table"
                focusFieldValue="123"
                summary="Offers Overview"
                onClick={() => {
                  setState({ tabName: "Offers" });
                }}
              />
            </Col>
          </Row>
        </Space>
      </div>
      {/* {details(state)} */}
      {state.tabName === "Invoices" && (
        <InvoiceTable
          onClose={() => {
            setState({ tabName: "" });
          }}
          open={state.tabName !== ""}
        />
      )}
      {state.tabName === "Consignment" && (
        <ConsignmentTable
          onClose={() => {
            setState({ tabName: "" });
          }}
          open={state.tabName !== ""}
        />
      )}
      {state.tabName === "Product" && (
        <ProductsTable
          open={state.tabName !== ""}
          onClose={() => {
            setState({ tabName: "" });
          }}
        />
      )}

      {state.tabName === "Offers" && (
        <OfferTable
          open={state.tabName !== ""}
          onClose={() => {
            setState({ tabName: "" });
          }}
        />
      )}
      {state.tabName === "Customer" && (
        <CustomerTable
          onClose={() => {
            setState({ tabName: "" });
          }}
          open={state.tabName !== ""}
          title={state.tabName}
          fetch={API[state.tabName].data}
          save={API[state.tabName].save}
          config={API[state.tabName].config}
        />
      )}
      {state.tabName === "Sales" && (
        <SimpleTable
          onClose={() => {
            setState({ tabName: "" });
          }}
          open={state.tabName !== ""}
          title={state.tabName}
          fetch={API[state.tabName].data}
          save={API[state.tabName].save}
          config={API[state.tabName].config}
          actions={{
            refresh: true,
            add: false,
            search: true,
            settings: true,
            export: true,
            delete: false,
            edit: false,
            select: false,
            import: false,
          }}
        />
      )}
      {/* {state.tabName !== "" && (
        <SimpleTable
          onClose={() => {
            setState({ tabName: "" });
          }}
          open={state.tabName !== ""}
          title={state.tabName}
          fetch={API[state.tabName].data}
          save={API[state.tabName].save}
          config={API[state.tabName].config}
        />
      )} */}
    </React.Fragment>
  );
};

export default signInRequired(Dashboard, "/signIn");
