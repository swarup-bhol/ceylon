import {SimpleTable} from "./SimpleTable";
import API from "../../api/APIWrapper";
import React from "react";
import {Button} from "antd";
import {EyeOutlined} from "@ant-design/icons";
import {CustomerOverview} from "./CustomerOerview";
import {LocalDatasource} from "@phantomit/datasource/lib/local/LocalDatasource";

const source = new LocalDatasource();


export const CustomerTable = ({open, onClose, title,fetch,save,config})=>{
    const [overview,setOverview] = React.useState({open:false,id:0})

    return <React.Fragment>
        <SimpleTable
            onClose={onClose}
            dataSource={source}
            open={open}
            title={title}
            fetch={fetch}
            save={save}
            config={config}
            customActions={(row,index)=>{
                return <Button key={`custom.${index}`} icon={<EyeOutlined />} onClick={()=>{
                    setOverview({open:true,id: row.company})
                }}/>
            }}
        />
        {
            overview.open && <CustomerOverview
                onClose={()=>{
                    setOverview({open: false, id: 0})
                }}
                customerId={overview.id}
                open={overview.open}
            />
        }
    </React.Fragment>
}