import gql from 'graphql-tag';

const verifyMFAEnforcementQuery = gql`      
    query verifyMFAEnforcement($username: String!, $sitekey: String!){
            verifyMFAEnforcement (username:$username,sitekey:$sitekey)
        }
    `;

const activateMFAQuery = gql`
    query activateMFA ($activate: Boolean!, $provider: String!){
            activateMFA (activate:$activate,provider:$provider)
    }
`;

const verifyTokenQuery = gql`
    query verifyToken ($password: String!, $provider: String!, $token: String!){
        verifyToken (password:$password,provider:$provider,token:$token)
    }
`;

export {verifyMFAEnforcementQuery,activateMFAQuery,verifyTokenQuery};
