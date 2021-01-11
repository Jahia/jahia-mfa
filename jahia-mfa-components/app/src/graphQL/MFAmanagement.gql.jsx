import gql from 'graphql-tag';


const prepareMFAQuery = gql `
    mutation prepareMFA ($password: String!, $provider: String!){
        mfa{
            prepareMFA (password:$password,provider:$provider)
        }
    }
`;

const activateMFAQuery = gql`
    mutation activateMFA ($activate: Boolean!, $provider: String!){
        mfa{
            activateMFA (activate:$activate,provider:$provider)
        }
    }
`;

const retrieveQRCodeQuery = gql `
    query retrieveQRCode ($password: String!){
        mfaOTP{
            retrieveQRCode (password:$password)
        }
    }
`;

const verifyMFAEnforcementQuery = gql`      
    query verifyMFAEnforcement($username: String!, $sitekey: String!){
        mfa{
            verifyMFAEnforcement (username:$username,sitekey:$sitekey)
            }
        }
    `;



const verifyTokenQuery = gql`
    query verifyToken ($password: String!, $provider: String!, $token: String!){
        mfa{
            verifyToken (password:$password,provider:$provider,token:$token)
        }
      
    }
`;

const verifyMFAStatusQuery = gql`
    query verifyMFAStatus{
        mfaOTP{
            verifyMFAStatus
        }
        
    }
`;



export {verifyMFAEnforcementQuery,activateMFAQuery,verifyTokenQuery,retrieveQRCodeQuery,prepareMFAQuery,verifyMFAStatusQuery};
