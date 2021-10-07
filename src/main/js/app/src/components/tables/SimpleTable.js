import React from "react";
import { LocalDatasource } from "@phantomit/datasource/lib/local/LocalDatasource";
import { Button, Divider, Drawer } from "antd";
import { Table } from "@phantomit/table";
import { SaveTableColumns } from "../../api/APIWrapper";

import { CloseOutlined } from "@ant-design/icons";

export const SimpleTable = ({
  open,
  onClose,
  fetch,
  save,
  config,
  title,
  customActions,
  dataSource,
  actions = {},
}) => {
  const source = dataSource || new LocalDatasource();

  return (
    <Drawer
      visible={open}
      width={`100vw`}
      closable={false}
      onClose={onClose}
      bodyStyle={{ padding: 0 }}
    >
      <Table
        source={source}
        scrollX={"max-content"}
        scrollY={"calc(100vh - 84px)"}
        fetch={async () => {
          const { data } = await fetch();          
          return data;
        }}
        save={async (row) => {          
          if (row.brand != "Brand" && row.company != "Company") {
            const {data} = await  save(row);
            return data;
          }
        }}
        delete={async (row) => {
          return row;
        }}
        columns={async () => {
          const {
            data: { tableId, columns },
          } = await config();
          console.log(columns);
          return columns;
        }}
        saveColumn={async (columns) => {
          const { data } = await SaveTableColumns(columns);
          return data;
        }}
        title={title}
        header={true}
        customActions={customActions}
        actions={{
          width: 160,
          refresh: false,
          add: true,
          search: true,
          settings: true,
          export: true,
          delete: false,
          edit: true,
          select: true,
          import: true,
          ...actions,
        }}
        headerButtons={[
          <Divider key={1} type={`vertical`} />,
          <Button
            key={2}
            icon={<CloseOutlined />}
            onClick={() => {
              onClose();
            }}
          />,
        ]}
      />
    </Drawer>
  );
};
