import React from 'react'
import {AmplifyAuthenticator,withAuthenticator, AmplifyContainer,AmplifySignIn,AmplifyFederatedSignIn} from "@aws-amplify/ui-react";
import {appendToCognitoUserAgent, Auth} from "@aws-amplify/auth";
import {AuthState, onAuthUIStateChange} from "@aws-amplify/ui-components";
import {useHistory} from "react-router-dom";


function signInStateAware(
    Component
) {
    return props => {
        const [signedIn, setSignedIn] = React.useState(false);
        const [_init, setInitState] = React.useState(false);
        const [_listener, setListener] = React.useState(undefined);
        React.useEffect(() => {
            appendToCognitoUserAgent('withAuthenticator');
            const ref = checkUser().catch(e=>{
                console.log(e)
            });
            setListener(ref)
        }, []);

        async function checkUser() {
            await setUser();
            return onAuthUIStateChange(authState => {
                if (authState === AuthState.SignedIn && signedIn === false) {
                    setSignedIn(true);
                } else if (authState === AuthState.SignedOut  && signedIn === true) {
                    setSignedIn(false);
                }
            });
        }

        async function setUser() {
            try {
                const user = await Auth.currentAuthenticatedUser();
                if (user) setSignedIn(true);
            } catch (err) { }
            finally {
                setInitState(true);
            }
        }
        if(_init)
            return <Component signedIn={signedIn} {...props}/>;
        else
            return <React.Fragment/>
    };
}

export const signInRequired = (Component, location)=>{

    const SignInRequired =  ({signedIn, ...args})=>{

        console.log(`signedIn = ${signedIn}`)
        const history = useHistory();
        if(signedIn)
            return <Component {...args}/>
        else
        history.push(location)
        return <React.Fragment/>
    }

    return signInStateAware(SignInRequired)
}

const SignInForm =  ({signedIn, ...args})=>{
    const history = useHistory();
    if(!signedIn)
        return <AmplifyAuthenticator {...args}>
            <AmplifySignIn slot="sign-in" hideSignUp  {...args}/>
        </AmplifyAuthenticator>
    else
        history.push('/')
    return <React.Fragment/>
}

export const SignInView = signInStateAware(SignInForm);

