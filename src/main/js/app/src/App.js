import React from 'react';
import Dashboard from "./components/Dashboard";
import {BrowserRouter as Router, Route, Switch, useHistory} from "react-router-dom";
import {SignInView} from "./auth/auth";
import {authProps} from "./config/aws";
import "antd/dist/antd.css";

function App() {
    return <React.Fragment>        
        <Router>
            <Switch>
                <Route path="/signIn">
                <SignInView {...authProps}/>
                </Route>
                <Route exact path={'/'}>
                    <Dashboard title="CEYLONSMUNICH 1.0"/>
                </Route>
            </Switch>
        </Router>
    </React.Fragment>;
}

export default App;
