import React from "react";
import axios from "axios";

import LoaderComponent from "./LoaderComponent";
import CarouselComponent from "./CarouselComponent";
import FiltersComponent from "./FiltersComponent";

class AppComponent extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      houses: [],
      defaultLink: "https://willhaben-parser.herokuapp.com/houses/?limit=25",
      link:
        window.link != null
          ? window.link
          : "https://willhaben-parser.herokuapp.com/houses/?limit=25",
      pageNumber: 1,
      loading: true,
    };
  }

  componentDidMount() {
    this.fetchdata();
  }

  fetchdata(link) {
    this.setState({ loading: true });
    axios.get(`${link != null ? link : this.state.link}`).then((res) => {
      const newHouses = res.data;
      this.setState({ houses: newHouses, loading: false });
    });
  }

  applyFilter(parameter, value) {
    let newLink;
    if (this.state.link.includes(parameter)) {
      let lastIndexOfParam = this.state.link.lastIndexOf(parameter + "=");
      let linkBegin = this.state.link.substring(0, lastIndexOfParam);
      let toProcess = this.state.link.substring(lastIndexOfParam + 1);
      let firstIndexOfNextParam = toProcess.indexOf("&");
      let linkEnd = "";
      if (firstIndexOfNextParam > 0) {
        linkEnd = toProcess.substring(firstIndexOfNextParam);
      }
      newLink = linkBegin + parameter + "=" + value + linkEnd;
    } else {
      newLink = this.state.link + "&" + parameter + "=" + value;
    }
    return newLink;
  }

  handleClick(parameter, value) {
    let newLink = this.applyFilter(parameter, value);
    this.fetchdata(newLink);
    this.setState({ link: newLink });
  }

  getFilter(parameter) {
    if (this.state.link.includes(parameter)) {
      let lastIndexOfParam = this.state.link.lastIndexOf(parameter + "=");
      let toProcess = this.state.link.substring(lastIndexOfParam);
      let valueStartIndex = toProcess.indexOf("=") + 1;
      let valueEndIndex = toProcess.indexOf("&");
      if (valueEndIndex > 0) {
        return toProcess.substring(valueStartIndex, valueEndIndex);
      } else {
        return toProcess.substring(valueStartIndex);
      }
    } else {
      return null;
    }
  }

  removeFilter(parameter) {
    if (this.state.link.includes(parameter)) {
      let lastIndexOfParam = this.state.link.lastIndexOf(parameter + "=") - 1;
      let linkBegin = this.state.link.substring(0, lastIndexOfParam);
      let toProcess = this.state.link.substring(lastIndexOfParam + 1);
      let valueEndIndex = toProcess.indexOf("&");
      let linkEnd = "";
      if (valueEndIndex > 0) {
        linkEnd = toProcess.substring(valueEndIndex);
      }
      let newLink = linkBegin + linkEnd;
      this.fetchdata(newLink);
      this.setState({ link: newLink });
    }
  }

  changeState(value) {
    this.changeLinkForLocation(value);
  }

  changeLinkForLocation(value) {
    let startIndexOfValue = this.state.link.indexOf("houses/") + 7;
    let linkBegin = this.state.link.substring(0, startIndexOfValue);
    let restOfLink = this.state.link.substring(startIndexOfValue);
    let parametersStartIndex = restOfLink.indexOf("?");
    let linkEnd = restOfLink.substring(parametersStartIndex);
    let newLink = linkBegin + value + linkEnd;
    this.fetchdata(newLink);
    this.setState({ link: newLink });
  }

  changeDistrict(state, district) {
    if (district != null && district.length > 0) {
      this.changeLinkForLocation(state + "/" + district);
    } else {
      this.changeLinkForLocation(state);
    }
  }

  changeLocation(state, district, location) {
    if (location != null && location.length > 0) {
      this.changeLinkForLocation(state + "/" + district + "/" + location);
    } else {
      this.changeLinkForLocation(state + "/" + district);
    }
  }

  getCurrentLocationFromLink() {
    let startIndexOfValue = this.state.link.indexOf("houses/") + 7;
    let restOfLink = this.state.link.substring(startIndexOfValue);
    let parametersStartIndex = restOfLink.indexOf("?");
    let toProcess = restOfLink.substring(0, parametersStartIndex);
    if (toProcess.length > 1) {
      let locationArray = toProcess.split("/");
      return locationArray;
    } else {
      return null;
    }
  }

  getStateFromLink() {
    let locationArray = this.getCurrentLocationFromLink();
    if (locationArray != null && locationArray.length > 0) {
      return this.getCurrentLocationFromLink()[0];
    } else {
      return null;
    }
  }

  getDistrictFromLink() {
    let locationArray = this.getCurrentLocationFromLink();
    if (locationArray != null && locationArray.length > 1) {
      return this.getCurrentLocationFromLink()[1];
    } else {
      return null;
    }
  }

  getLocationFromLink() {
    let locationArray = this.getCurrentLocationFromLink();
    if (locationArray != null && locationArray.length > 2) {
      return this.getCurrentLocationFromLink()[2];
    } else {
      return null;
    }
  }

  getShareLink(house) {
    return (
      "https://willhaben-parser.herokuapp.com/index.html?link=https://willhaben-parser.herokuapp.com/houses/id/" +
      house.id
    );
  }

  getIdFromLink() {
    if (this.state.link.includes("houses/id/")) {
      let startIndexOfValue = this.state.link.indexOf("houses/id/") + 10;
      console.log(startIndexOfValue);
      console.log(this.state.link.substring(startIndexOfValue));
      if (startIndexOfValue > 0) {
        return this.state.link.substring(startIndexOfValue);
      }
    }
    return null;
  }

  resetLink() {
    this.setState({ link: this.state.defaultLink });
  }

  getMapsLink(house) {
    return `https://www.google.com/maps/search/${house.location}`;
  }

  render() {
    return (
      <div style={{ margin: "0px", backgroundColor: "white" }}>
        {this.state.loading ? (
          <LoaderComponent></LoaderComponent>
        ) : (
          <div style={{ backgroundColor: "white" }}>
            <CarouselComponent
              houses={this.state.houses}
              handleClick={(parameter, value) =>
                this.handleClick(parameter, value)
              }
              changeLocation={(state, district, location) =>
                this.changeLocation(state, district, location)
              }
              changeDistrict={(state, district) =>
                this.changeDistrict(state, district)
              }
              changeState={(state) => this.changeState(state)}
              getMapsLink={(house) => this.getMapsLink(house)}
              getShareLink={(house) => this.getShareLink(house)}
            ></CarouselComponent>
            <FiltersComponent
              getFilter={(param) => this.getFilter(param)}
              removeFilter={(param) => this.removeFilter(param)}
              getStateFromLink={() => this.getStateFromLink()}
              getDistrictFromLink={() => this.getDistrictFromLink()}
              getLocationFromLink={() => this.getLocationFromLink()}
              getIdFromLink={() => this.getIdFromLink}
              changeLocation={(state, district, location) =>
                this.changeLocation(state, district, location)
              }
              changeDistrict={(state, district) =>
                this.changeDistrict(state, district)
              }
              changeState={(state) => this.changeState(state)}
              resetLink={this.resetLink}
            ></FiltersComponent>
          </div>
        )}
      </div>
    );
  }
}

export default AppComponent;
