import React from "react";
import Coverflow from "react-coverflow";
import HousePhotoComponent from "./HousePhotoComponent";
import HouseDataComponent from "./HouseDataComponent";

function renderCarousel(props) {
  return (
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
      {props.houses.map((house) => (
        <div style={{ backgroundColor: "white" }}>
          <HousePhotoComponent house={house}></HousePhotoComponent>
          <HouseDataComponent
            house={house}
            handleClick={(parameter, value) =>
              props.handleClick(parameter, value)
            }
            changeLocation={(state, district, location) =>
              props.changeLocation(state, district, location)
            }
            changeDistrict={(state, district) =>
              props.changeDistrict(state, district)
            }
            changeState={(state) => props.changeState(state)}
            getMapsLink={props.getMapsLink}
            getShareLink={props.getShareLink}
          ></HouseDataComponent>
        </div>
      ))}
    </Coverflow>
  );
}

export default renderCarousel;
