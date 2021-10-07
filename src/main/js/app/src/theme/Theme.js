import { MuiThemeProvider } from "@material-ui/core";
import grey from '@material-ui/core/colors/grey'
import {
    CssBaseline,
    createMuiTheme
} from "@material-ui/core";
import React from "react";


const theme = createMuiTheme({
    palette: {
        type:'light'
    },
    appBar: {
        color: "#607d8b",
        textColor: "rgba(255, 255, 255, 0.87)"
    }
});

export const DarkTheme = ({children})=>{
    return <MuiThemeProvider theme={theme}>
        <CssBaseline />
        {children}
    </MuiThemeProvider>
}