import React from "react";
import ReactDOM from "react-dom";
import AppComponent from "./AppComponent";
import { CookiesProvider } from "react-cookie";

ReactDOM.render(
  <CookiesProvider>
    <AppComponent />
  </CookiesProvider>,
  document.getElementById("react")
);
