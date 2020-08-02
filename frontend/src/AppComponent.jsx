import React from "react";
import axios from "axios";
import Coverflow from "react-coverflow";

class AppComponent extends React.Component {
  state = {
    houses: [],
  };

  componentDidMount() {
    axios
      .get(
        `https://willhaben-parser.herokuapp.com/houses/oberösterreich?postedToday=1`
      )
      .then((res) => {
        const houses = res.data;
        this.setState({ houses });
      });
  }

  render() {
    return (
      <Coverflow
        displayQuantityOfSide={2}
        navigation
        infiniteScroll
        enableHeading
        media={{
          "@media (max-width: 900px)": {
            width: "900px",
            height: "600px",
          },
          "@media (min-width: 900px)": {
            width: "1920px",
            height: "900px",
          },
        }}
      >
        {this.state.houses.map((house) => (
          <img
            src={house.pictureLink}
            alt={
              new Intl.NumberFormat("de-DE", {
                style: "currency",
                currency: "EUR",
                minimumFractionDigits: 0,
                maximumFractionDigits: 0,
              }).format(house.price) +
              (house.size != null ? " | " + house.size + "m²" : "") +
              (house.groundArea != null
                ? " | " + house.groundArea + " m²"
                : "") +
              (house.rooms != null ? " | " + house.rooms + " rooms" : " ") +
              (house.objectType != null ? " | " + house.objectType : "") +
              " | " +
              house.locationName +
              ", " +
              house.districtName +
              ", " +
              house.stateName
            }
            data-action={house.link}
          />
        ))}
      </Coverflow>
    );
  }
}

export default AppComponent;
