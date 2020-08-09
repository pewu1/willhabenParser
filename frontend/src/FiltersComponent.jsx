import React from "react";
import { Button } from "antd";

function getFilters(props) {
  return (
    <div>
      {props.getFilter("maxPrice") != null ? (
        <Button onClick={(event) => props.removeFilter("maxPrice")}>
          {"< "}
          {new Intl.NumberFormat("de-DE", {
            style: "currency",
            currency: "EUR",
            minimumFractionDigits: 0,
            maximumFractionDigits: 0,
          }).format(props.getFilter("maxPrice"))}
        </Button>
      ) : (
        ""
      )}

      {props.getFilter("size") != null ? (
        <Button onClick={(event) => props.removeFilter("size")}>
          {"> "}
          {props.getFilter("size") + "m²"}
        </Button>
      ) : (
        ""
      )}

      {props.getFilter("ground") != null ? (
        <Button onClick={(event) => props.removeFilter("ground")}>
          {"> "}
          {props.getFilter("ground") + "m²"}
        </Button>
      ) : (
        ""
      )}

      {props.getFilter("rooms") != null ? (
        <Button onClick={(event) => props.removeFilter("rooms")}>
          {"> "}
          {props.getFilter("rooms") + "rooms"}
        </Button>
      ) : (
        ""
      )}

      {props.getFilter("type") != null ? (
        <Button onClick={(event) => props.removeFilter("type")}>
          {props.getFilter("type")}
        </Button>
      ) : (
        ""
      )}

      {props.getFilter("postedAfter") != null ? (
        <Button onClick={(event) => props.removeFilter("postedAfter")}>
          {props.getFilter("postedAfter")}
        </Button>
      ) : (
        ""
      )}

      {props.getStateFromLink() != null ? (
        <Button onClick={(event) => props.changeState("")}>
          {props.getStateFromLink()}
        </Button>
      ) : (
        ""
      )}

      {props.getDistrictFromLink() != null ? (
        <Button
          onClick={(event) =>
            props.changeDistrict(props.getStateFromLink(), "")
          }
        >
          {props.getDistrictFromLink()}
        </Button>
      ) : (
        ""
      )}

      {props.getLocationFromLink() != null ? (
        <Button
          onClick={(event) =>
            props.changeLocation(
              props.getStateFromLink(),
              props.getDistrictFromLink(),
              ""
            )
          }
        >
          {props.getLocationFromLink()}
        </Button>
      ) : (
        ""
      )}
      {props.getIdFromLink() != null ? (
        <Button onClick={(event) => props.resetLink()}>
          ID: {props.getIdFromLink()}
        </Button>
      ) : (
        ""
      )}
    </div>
  );
}

export default getFilters;
