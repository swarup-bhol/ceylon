/*global process*/
import axios from 'axios'
import {message} from "antd";
import {Auth} from 'aws-amplify'

/**/
if(process.env.REACT_APP_SERVER_URL){
    axios.defaults.baseURL = process.env.REACT_APP_SERVER_URL;
    axios.defaults.headers.common['Access-Control-Allow-Origin'] = '*';
    axios.defaults.headers.common['Access-Control-Allow-Headers'] = '*';
}


axios.interceptors.request.use(async (cfg)=>{
    const session = await Auth.currentSession();
    const token = await session.getAccessToken();
    cfg.headers['Authorization'] = `Bearer ${token.getJwtToken()}`
    return cfg;
})

axios.interceptors.response.use(res=> res, err=>{
    message.error(err.message);
});


/**/

export const ProductData = ()=>{
    return axios.get('/api/products')
};

export const SaveProductionData = (data) =>{
    return axios.post("/api/products/update",data);
};

export const SaveProduct = (data) =>{
    return axios.post("/api/product/save",data);
};

export const ProductDataConfig = ()=>{
    return axios.get("/api/configs/product.json");
};
export const ProductSchemaConfig = ()=>{
    return axios.get("/api/configs/product_schema.json");
};


export const CustomerData = ()=>{
    return axios.get('/api/customers')
};

export const SaveCustomerData = (data) =>{
    return axios.post("/api/customer/save",data);
};
export const CustomerDataConfig = ()=>{
    return axios.get("/api/configs/customer.json");
};
export const CustomerSchemaConfig = ()=>{
    return axios.get("/api/configs/customer_schema.json");
};

export const SalesData = ()=>{
    return axios.get('/api/sales')
};

export const SaveSalesData = (data) =>{
    return axios.post("/api/sales/update",data);
};
export const SalesDataConfig = ()=>{
    return axios.get("/api/configs/sales_view.json");
};

export const Enums = ()=>{
    return axios.get("/api/enums");
};

export const SaveEnum = (data)=>{
    return axios.post("/api/enums/update",data);
}

export const Currency = ()=>{
    return axios.get("/api/currency");
};

export const SaveCurrency = (data)=>{
    return axios.post("/api/currency/add",data);
}

export const CurrencyConfig = ()=>{
    return axios.get("/api/configs/currency.json");
};
export const CurrencySchemaConfig = ()=>{
    return axios.get("/api/configs/currency_schema.json");
};

export const ExchangeRates = ()=>{
    return axios.get("/api/exchangerate");
};

export const ExchangeRatesConfig = ()=>{
    return axios.get("/api/configs/exchange.json");
};

export const SaveExchangeRates = (data)=>{
    return axios.post("/api/exchangerate/batchupdate",data);
}


export const Invoices = ()=>{
    return axios.get("/api/invoices");
};

export const InvoiceConfig = ()=>{
    return axios.get("/api/configs/invoice.json");
};

export const SaveInvoice = (data)=>{
    return axios.post("/api/invoices/update",data);
}


export const CompleteInvoice = (data)=>{
    return axios.post("/api/invoices/complete",data);
}


export const RefundInvoice = (data)=>{
    return axios.post("/api/invoices/refund",data);
}



export const Consignment = ()=>{
    return axios.get("/api/consignment");
};

export const ConsignmentConfig = ()=>{
    return axios.get("/api/configs/consignment.json");
};

export const SaveConsignment = (data)=>{
    return axios.post("/api/consignment/update",data);
}


export const Offers = ()=>{
    return axios.get("/api/offers");
};

export const OffersConfig = ()=>{
    return axios.get("/api/configs/offer.json");
};

export const SaveOffer = (data)=>{
    return axios.post("/api/offer/save",data);
}


export const Parameters = ()=>{
    return axios.get("/api/parameters");
};

export const SaveParameters = (data)=>{
    return axios.post("/api/parameters/update",data);
}

export const SaveTableConfig = (data)=>{
    return axios.post("/api/configs/update",data);
};

export const SaveTableColumns = (data)=>{
    return axios.post("/api/column/save",data);
};

export const downloadInvoice = (id)=>{
    return axios({
        url: `/api/pdf/${id}/download`, //your url
        method: 'GET',
        responseType: 'blob', // important
    }).then((response) => {
        const fileName = response.headers["x-suggested-filename"] || "Invoice.pdf"
        console.log(response)
        const blobStore = new Blob([response.data], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blobStore);
        const params = `scrollbars=no,resizable=no,status=no,location=no,toolbar=no,menubar=no`;

        window.open(url, `${fileName}.pdf`, params)
    });
};


export const downloadQr = (products)=>{
    axios({
        url: `/api/pdf/qr/download`, //your url
        method: 'GET',
        params: {
            products: products
        },
        responseType: 'blob', // important
    }).then((response) => {
        const fileName = response.headers["x-suggested-filename"] || "Invoice.pdf"
        console.log(response)
        const blobStore = new Blob([response.data], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blobStore);
        const params = `scrollbars=no,resizable=no,status=no,location=no,toolbar=no,menubar=no`;

        window.open(url, `${fileName}.pdf`, params)

    });
};


export const product_overview = (id)=>{
    return axios.get(`/api/products/${id}/overview`);
}


export const customer_overview = (id)=>{
    return axios.get(`/api/customers/${id}`);
}

export const product_overview_config = ()=>{
    return axios.get("/api/configs/product_overview.json");
}



export const productUpload = (data)=>{
    return axios.post("/api/products/upload", data);
}




export const customerUpload = (data)=>{
    return axios.post("/api/customer/upload", data);
}




const API = {
    Product:{
        data:ProductData,
        save:SaveProductionData,
        config:ProductDataConfig,
        schema:ProductSchemaConfig
    },
    Customer:{
        data:CustomerData,
        save:SaveCustomerData,
        config:CustomerDataConfig,
        schema:CustomerSchemaConfig
    },
    Sales:{
        data:SalesData,
        save:SaveSalesData,
        config:SalesDataConfig,
        schema:()=>{}
    },
    ExchangeRates:{
        data:ExchangeRates,
        save:SaveExchangeRates,
        config:ExchangeRatesConfig
    },
    Currency:{
        data:Currency,
        save:SaveCurrency,
        config:CurrencyConfig
    },
    Invoice:{
        data:Invoices,
        save:SaveInvoice,
        config:InvoiceConfig
    },
    Consignment:{
        data:Consignment,
        save:SaveConsignment,
        config:ConsignmentConfig
    },
    Offers:{
        data: Offers,
        save: SaveOffer,
        config: OffersConfig
    }
};
export default API;
