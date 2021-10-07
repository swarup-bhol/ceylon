import React from "react";
import PropTypes from 'prop-types';
import {Card, Statistic, Space} from "antd";


export const Tile = ({
                          title,
                          focusFieldName,
                          focusFieldValue,
                          summary,
                          tileIcon,
                          onClick=()=>{}
                      })=>{

    return <Card
        title={title}
        hoverable
        onClick={()=>{
            onClick();
        }}
        cover={<Space style={{width: '100%', padding: 8}}>
            <Card>
                <img alt="icon" src={tileIcon} width={120} height={120}/>
            </Card>
            <Card style={{width: '100%'}}>
                <Statistic
                    title={focusFieldName}
                    value={focusFieldValue}
                    precision={2}
                />
            </Card>
        </Space>}

    >
        {summary}
    </Card>

}
