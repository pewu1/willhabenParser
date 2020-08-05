import React from "react";
import axios from "axios";
import Coverflow from "react-coverflow";
import { Button } from "antd";
import {
  EmailShareButton,
  FacebookShareButton,
  WhatsappShareButton,
} from "react-share";

import { EmailIcon, FacebookIcon, WhatsappIcon } from "react-share";

class AppComponent extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      houses: [],
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
    console.log("https://willhaben-parser.herokuapp.com/houses/id/" + house.id);
    return "https://willhaben-parser.herokuapp.com/houses/id/" + house.id;
  }

  render() {
    return (
      <div style={{ margin: "0px", backgroundColor: "white" }}>
        {this.state.loading ? (
          <div>
            <div style={{ textAlign: "center" }}>
              Application is starting, this can take up to two minutes...
            </div>
            <div
              style={{
                display: "flex",
                justifyContent: "center",
                height: "calc(100vh - 50px)",
              }}
            >
              <img
                src="https://upload.wikimedia.org/wikipedia/commons/c/cf/SVG_animated_loading_icon2.svg"
                alt="Loading..."
              />
            </div>
          </div>
        ) : (
          <div style={{ backgroundColor: "white" }}>
            <Coverflow
              active={1}
              displayQuantityOfSide={1}
              navigation={false}
              enableHeading={false}
              media={{
                "@media (max-width: 900px)": {
                  width: "600px",
                  height: "600px",
                },
                "@media (min-width: 900px)": {
                  width: "100%",
                  height: "95vh",
                },
              }}
            >
              {this.state.houses.map((house) => (
                <div style={{ backgroundColor: "white" }}>
                  <div>
                    <a
                      href={house.link}
                      target="_blank"
                      rel="noopener noreferrer"
                    >
                      <img
                        src={
                          house.pictureLink != null
                            ? house.pictureLink
                            : "https://upload.wikimedia.org/wikipedia/commons/a/ac/No_image_available.svg"
                        }
                        width="100%"
                        style={{ maxHeight: "400px" }}
                        alt={house.link}
                        data-action={house.link}
                      />
                    </a>
                  </div>
                  <div style={{ float: "left" }}>
                    <Button
                      type="primary"
                      onClick={(event) =>
                        this.handleClick("maxPrice", house.price)
                      }
                    >
                      {new Intl.NumberFormat("de-DE", {
                        style: "currency",
                        currency: "EUR",
                        minimumFractionDigits: 0,
                        maximumFractionDigits: 0,
                      }).format(house.price)}
                    </Button>
                    {house.size != null ? (
                      <Button
                        type="primary"
                        onClick={(event) =>
                          this.handleClick("size", house.size)
                        }
                      >
                        {house.size} m²
                      </Button>
                    ) : (
                      ""
                    )}
                    {house.groundArea != null ? (
                      <Button
                        type="primary"
                        onClick={(event) =>
                          this.handleClick("ground", house.groundArea)
                        }
                      >
                        {house.groundArea} m²
                      </Button>
                    ) : (
                      ""
                    )}
                    {house.rooms != null ? (
                      <Button
                        type="primary"
                        onClick={(event) =>
                          this.handleClick("rooms", house.rooms)
                        }
                      >
                        {house.rooms} rooms
                      </Button>
                    ) : (
                      ""
                    )}
                    {house.objectType != null ? (
                      <Button
                        type="primary"
                        onClick={(event) =>
                          this.handleClick("type", house.objectType)
                        }
                      >
                        {house.objectType}
                      </Button>
                    ) : (
                      ""
                    )}
                  </div>
                  <div style={{ float: "left" }}>
                    <Button type="primary">{house.postalCode}</Button>
                    <Button
                      type="primary"
                      onClick={(event) =>
                        this.changeLocation(
                          house.stateName,
                          house.districtName,
                          house.locationName
                        )
                      }
                    >
                      {house.locationName}
                    </Button>
                    <Button
                      type="primary"
                      onClick={(event) =>
                        this.changeDistrict(house.stateName, house.districtName)
                      }
                    >
                      {house.districtName}
                    </Button>
                    <Button
                      type="primary"
                      onClick={(event) => this.changeState(house.stateName)}
                    >
                      {house.stateName}
                    </Button>
                  </div>
                  <div style={{ float: "left" }}>
                    <Button
                      type="primary"
                      onClick={(event) =>
                        this.handleClick(
                          "postedAfter",
                          house.editDate.replace(".", "")
                        )
                      }
                    >
                      {house.editDate}
                    </Button>
                  </div>
                  <div
                    style={{
                      float: "right",
                      textAlign: "right",
                      fontSize: "10px",
                    }}
                  >
                    <FacebookShareButton url={this.getShareLink(house)}>
                      <FacebookIcon size="20"></FacebookIcon>
                    </FacebookShareButton>
                    <WhatsappShareButton url={house.link}>
                      <WhatsappIcon size="20"></WhatsappIcon>
                    </WhatsappShareButton>
                    <EmailShareButton url={house.link}>
                      <EmailIcon size="20"></EmailIcon>
                    </EmailShareButton>
                  </div>
                </div>
              ))}
            </Coverflow>

            {this.getFilter("maxPrice") != null ? (
              <Button onClick={(event) => this.removeFilter("maxPrice")}>
                {"< "}
                {new Intl.NumberFormat("de-DE", {
                  style: "currency",
                  currency: "EUR",
                  minimumFractionDigits: 0,
                  maximumFractionDigits: 0,
                }).format(this.getFilter("maxPrice"))}
              </Button>
            ) : (
              ""
            )}

            {this.getFilter("size") != null ? (
              <Button onClick={(event) => this.removeFilter("size")}>
                {"> "}
                {this.getFilter("size") + "m²"}
              </Button>
            ) : (
              ""
            )}

            {this.getFilter("ground") != null ? (
              <Button onClick={(event) => this.removeFilter("ground")}>
                {"> "}
                {this.getFilter("ground") + "m²"}
              </Button>
            ) : (
              ""
            )}

            {this.getFilter("rooms") != null ? (
              <Button onClick={(event) => this.removeFilter("rooms")}>
                {"> "}
                {this.getFilter("rooms") + "rooms"}
              </Button>
            ) : (
              ""
            )}

            {this.getFilter("type") != null ? (
              <Button onClick={(event) => this.removeFilter("type")}>
                {this.getFilter("type")}
              </Button>
            ) : (
              ""
            )}

            {this.getFilter("postedAfter") != null ? (
              <Button onClick={(event) => this.removeFilter("postedAfter")}>
                {this.getFilter("postedAfter")}
              </Button>
            ) : (
              ""
            )}

            {this.getStateFromLink() != null ? (
              <Button onClick={(event) => this.changeState("")}>
                {this.getStateFromLink()}
              </Button>
            ) : (
              ""
            )}

            {this.getDistrictFromLink() != null ? (
              <Button
                onClick={(event) =>
                  this.changeDistrict(this.getStateFromLink(), "")
                }
              >
                {this.getDistrictFromLink()}
              </Button>
            ) : (
              ""
            )}

            {this.getLocationFromLink() != null ? (
              <Button
                onClick={(event) =>
                  this.changeLocation(
                    this.getStateFromLink(),
                    this.getDistrictFromLink(),
                    ""
                  )
                }
              >
                {this.getLocationFromLink()}
              </Button>
            ) : (
              ""
            )}
          </div>
        )}
      </div>
    );
  }
}

export default AppComponent;
