import React from "react";
import Coverflow from "react-coverflow";

class Carousell extends React.Component {
  render() {
    return (
      <Coverflow
        displayQuantityOfSide={2}
        navigation
        infiniteScroll
        enableHeading
        media={{
          "@media (max-width: 900px)": {
            width: "600px",
            height: "300px",
          },
          "@media (min-width: 900px)": {
            width: "960px",
            height: "600px",
          },
        }}
      >
        <img
          src="https://cache.willhaben.at/mmo/5/391/263/665_1117323998_hoved.jpg"
          alt="4320 Perg, Oberösterreich"
          data-action="https://www.willhaben.at/iad/immobilien/d/haus-kaufen/oberoesterreich/perg/top-reihenhaus-in-perg-central-village-391263665/"
        />
        <img
          src="https://cache.willhaben.at/mmo/7/337/642/977_49873654_hoved.jpg"
          alt="3413 Kirchbach, Tulln, Niederösterreich"
          data-action="https://www.willhaben.at/iad/immobilien/d/haus-kaufen/niederoesterreich/tulln/idylle-am-waldrand-naehe-wien-mit-pool-provisioinsfrei-337642977/"
        />
        <img
          src="https://cache.willhaben.at/mmo/0/395/360/410_819651550_hoved.jpg"
          alt="9400 Wolfsberg, Kärnten"
          data-action="https://www.willhaben.at/iad/immobilien/d/haus-kaufen/kaernten/wolfsberg/-modernes-reihenhaus-in-ziegelmassiv-in-bevorzugter-lage-in-wolfsberg-395360410/"
        />
        <img
          src="https://cache.willhaben.at/mmo/9/397/745/479_-2136548046_hoved.jpg"
          alt="4621 Leombach, Wels-Land, Oberösterreich"
          data-action="https://www.willhaben.at/iad/immobilien/d/haus-kaufen/oberoesterreich/wels-land/doppelhaushaelfte-auf-traumhaftem-bachgrundstueck-397745479/"
        />
      </Coverflow>
    );
  }
}

export default AppComponent;
