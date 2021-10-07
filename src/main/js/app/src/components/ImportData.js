import React from "react";
import { Alert, Button, Modal, Upload } from "antd";
import { InboxOutlined } from "@ant-design/icons";

export const ImportData = ({ upload }) => {
  const [data, setData] = React.useState([]);
  const [visible, setVisible] = React.useState(false);

  return (
    <React.Fragment>
      <Button
        onClick={() => {
          setVisible(true);
        }}
      >
        Upload Excel File
      </Button>
      {visible && (
        <Modal
          centered={true}
          visible={true}
          onCancel={() => setVisible(false)}
          onOk={() => setVisible(false)}
        >
          <Upload.Dragger
            accept={".xlsx"}
            customRequest={async (data) => {                
              const formData = new FormData();
              formData.append("file", data.file);
              const info = await upload(formData);              
              setData(info.data);
            }}
            multiple={false}
            listType="picture"
            showUploadList={false}
          >
            <p className="ant-upload-drag-icon">
              <InboxOutlined />
            </p>
            <p className="ant-upload-text">
              Click or drag file to this area to upload
            </p>
          </Upload.Dragger>
          <Alert
            type={"success"}
            message={`${data.length} records uploaded successfully`}
          />
        </Modal>
      )}
    </React.Fragment>
  );
};
