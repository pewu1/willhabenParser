import React from "react";
import axios from "axios";
import Coverflow from "react-coverflow";

class AppComponent extends React.Component {
  state = {
    houses: [],
  };

  componentDidMount() {
    axios
      .get(`https://willhaben-parser.herokuapp.com/houses/?postedToday=1`)
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
            width: "600px",
            height: "300px",
          },
          "@media (min-width: 900px)": {
            width: "960px",
            height: "600px",
          },
        }}
      >
        {this.state.houses.map((house) => (
          <img
            src="{house.pictureLink}"
            alt="{house.location}"
            data-action="{house.link}"
          />
        ))}
      </Coverflow>
    );
  }
}

export default AppComponent;
