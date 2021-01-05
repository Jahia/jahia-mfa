import gql from 'graphql-tag';


const prepareMFAQuery = gql `
    query prepareMFA ($password: String!, $provider: String!){
        prepareMFA (password:$password,provider:$provider)
    }
`;

const retrieveQRCodeQuery = gql `
    query retrieveQRCode ($password: String!){
        retrieveQRCode (password:$password)
    }
`;

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

const verifyMFAStatusQuery = gql`
    query verifyMFAStatus{
        verifyMFAStatus
    }
`;



export {verifyMFAEnforcementQuery,activateMFAQuery,verifyTokenQuery,retrieveQRCodeQuery,prepareMFAQuery,verifyMFAStatusQuery};
