
import React from 'react';
import './App.css';
import MultiStepForm from "./components/MultiStepForm.jsx";
import {ApolloProvider, ApolloClient, InMemoryCache,HttpLink} from '@apollo/client';
import { config } from "./config";
import {GlobalStyle} from '@jahia/moonstone';


if (config.dxHost){
    var uriGraphQL = config.dxHost + "/modules/graphql"
}else{
    var uriGraphQL  = "/modules/graphql"
}

const httpLink = new HttpLink({
    uri: uriGraphQL
});


const client = new ApolloClient({
    link: httpLink,
    cache: new InMemoryCache(),
});


function App() {
  return (
      <ApolloProvider client={client}>
          <GlobalStyle/>
    <div className="App">
          <header className="App-header">
              <div>
                    <MultiStepForm />
              </div>
          </header>
    </div>
      </ApolloProvider>
  );
}

export default App;
