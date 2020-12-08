import logo from './logo.svg';
import './App.css';
import MultiStepForm from "./components/MultiStepForm.js";
import {StylesProvider} from "@material-ui/core";
import {ApolloProvider} from '@apollo/client';
import ApolloClient  from 'apollo-client';
import {InMemoryCache} from "apollo-cache-inmemory";
import { config } from "./config";
import { HttpLink } from "apollo-link-http";


const httpLink = new HttpLink({
    uri: config.dxHost + "/modules/graphql",
});

const client = new ApolloClient({
    link: httpLink,
    cache: new InMemoryCache(),
});


function App() {
  return (
      <ApolloProvider client={client}>
    <div className="App">
      <header className="App-header">
        <MultiStepForm />
      </header>
    </div>
      </ApolloProvider>
  );
}

export default App;
