import React from "react";
import axios from "axios";
import Coverflow from "react-coverflow";
import { Button } from "antd";

class AppComponent extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      houses: [],
      link: "https://willhaben-parser.herokuapp.com/houses/?limit=25",
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

  handleClick(parameter, value) {
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
    this.fetchdata(newLink);
    this.setState({ link: newLink });
  }

  render() {
    return (
      <div style={{ margin: "0px" }}>
        {this.state.loading ? (
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
        ) : (
          <Coverflow
            displayQuantityOfSide={2}
            navigation={false}
            enableHeading={false}
            media={{
              "@media (max-width: 900px)": {
                width: "600px",
                height: "600px",
              },
              "@media (min-width: 900px)": {
                width: "100%",
                height: "90vh",
              },
            }}
          >
            {this.state.houses.map((house) => (
              <div style={{ backgroundColor: "white" }}>
                <a href={house.link} target="_blank" rel="noopener noreferrer">
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
                <Button
                  type="primary"
                  onClick={(event) => this.handleClick("maxPrice", house.price)}
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
                    onClick={(event) => this.handleClick("size", house.size)}
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
                    onClick={(event) => this.handleClick("rooms", house.rooms)}
                  >
                    {house.rooms} rooms
                  </Button>
                ) : (
                  ""
                )}
                <br></br>
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
                <br></br>
                <Button type="primary">{house.postalCode}</Button>
                <Button type="primary">{house.locationName}</Button>
                <Button type="primary">{house.districtName}</Button>
                <br></br>
                <span>
                  <Button type="primary">{house.stateName}</Button>
                  <div float="right">{house.editDate}</div>
                </span>
              </div>
            ))}
          </Coverflow>
        )}
      </div>
    );
  }
}

export default AppComponent;
