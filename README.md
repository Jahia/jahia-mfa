# Documentation
This document provides documentation on the Jahia Multi Factor Authentication (MFA) module

Github Repository: https://github.com/Jahia/jahia-mfa

# MFA Core
## Description
The module provides the core functionality of Multi Factor Authentication. It provides a MFA service that can register different type of MFA providers (OTP, SMS, Email, Phone...). It also implement the core authentication mechanism at the Jahia level to leverage MFA.

## Jahia MFA Service
This service can register/remove MFA providers. It also exposes the core methods or our MFA service:

 - Verify the token sent by the user against the TOTP key thanks to the related Jahia MFA provider
 - Prepare MFA of the related user without activating, thanks to the related Jahia MFA provider
 - Activate MFA of the related user thanks to the related Jahia MFA provider
 - Deactivate MFA of the related user thanks to the related Jahia MFA provider
 - Check if the user has activated MFA

## Jahia MFA Servlet
The Servlet MFA is used to override the default Jahia login page in order to add the MFA Code input field

The MFA Servlet endpoint is "/mfa"

## Jahia MFA Valve
The Jahia MFA Valve handle the authentication part of the module. It intercepts authentication coming from the MFA login forms and validate the MFA token.

It retrieves the secret key stored under the user and uses it to verify the MFA token provided by the user.

## User Node Extension
## Jahia Site Configuration
The site node is extended with additional properties:

### enforce MFA (boolean)
The boolean decides if all users accessing the website must have MFA setup in order to access it. If MFA is not set up for a user, this user will won't be able to access the website until MFA has been activated

### pageMFAactivation (weakreference)
This weakreference defines the Jahia page that will be displayed if MFA is enforced and a user has not activated it's MFA.

This page should contains instructions to help the user setup its MFA. The MFA component has been built for that.

## Jahia MFA Filter
the MFA Filter is called when MFA is enforce at the site level. It redirects the user to the page defined bu the  "pageMFAactivation (weakreference)"

## GraphQL EndPoints
The MFA Core module expose the following GraphQL endpoints:

    mutation{
        mfa{
            prepareMFA (password:$password,provider:$provider)
        }
    }

    mutation{
        mfa{
            activateMFA (activate:$activate,provider:$provider)
        }
    }
  
    query{
        mfa{
            verifyMFAEnforcement (username:$username,sitekey:$sitekey)
        }
    }

    query{
        mfa{
            verifyToken (password:$password,provider:$provider,token:$token)
        } 
    }
# MFA OTP Provider
## Description
The module implements an MFA Provider that will be register by our MFA Service. This specific MFA Provider uses One Time Password (OTP).

## Jahia MFA OTP Provider

## GraphQL EndPoints

    query{
        mfaOTP{
            retrieveQRCode (password:$password)
        }
    }
    
    query{
        mfaOTP{
            verifyMFAStatus
        }
    }

# MFA OTP Component
## Description
This module contains a Jahia component built in react that allows to user to register to the OPT MFA authentication by providing their logging authentication.

The component provides the ability to:

 - Prepare MFA: The component will create a secret key for the user, encrypt it and store it under the user node 
 - Validate MFA: Leveraging the secret key, the component will generate a QR Code. The user will scan this QR Code with an authenticator 
   (ie Google Authenticator) in order to retrieve a code. By entering the code, in the same screen, this will validate that the user has successfully set up its authenticator.
 - Activate MFA: Once validated, the user has now the option the activate MFA. Once activated, the user will now have to enter its MFA 
   code on top of its credentials.
 - Deactivate MFA: The component will remove the encrypted secret key store under the user node.


# Release Notes
Only mfa-core and mfa-otp-provider are included in the release

## Scope V1
- MFA Package containing mfa-core and mfa-otp-provider 
- Ability to register MFA provider such as mfa-otp-provider
    - IMPORTANT, only 6 digits are supported
- Activation 2FA with OTP provided by Google Authenticator and renewed on time basis
    - support of LDAP :
        - backend 
        - UI
- Authentication valve at site and global level 
- Support of default login servlet
- MFA custom login servlet
- Encryption of OTP secret key leveraging the user password
- Deactivation of MFA upon password change for JCR users
- GraphQL endpoints:
    - prepare MFA
    - activate MFA
    - verify MFA Token 
    - retrieve MFA QRCode
    - verify MFA Status 

## Scope V2
- MFA enforcement at site or platform level
- When login fails through /cms/login, redirect to /mfa instead of returning an error
- Change password GraphQL endpoint
- Automatically created a page for MFA registration
- Email the user when the MFA is activated or deactivated

## Scope Vn
- MFA Registration as servlet
- recovery codes
- internationalization
