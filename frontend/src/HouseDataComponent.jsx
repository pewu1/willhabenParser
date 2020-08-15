import React from "react";

import { Button } from "antd";
import {
  EmailShareButton,
  FacebookShareButton,
  WhatsappShareButton,
} from "react-share";
import { EmailIcon, FacebookIcon, WhatsappIcon } from "react-share";

function getHouseData(props) {
  return (
    <div style={{ backgroundColor: "white" }}>
      <div style={{ float: "left" }}>
        <Button
          type="primary"
          onClick={(event) => props.handleClick("maxPrice", props.house.price)}
        >
          {new Intl.NumberFormat("de-DE", {
            style: "currency",
            currency: "EUR",
            minimumFractionDigits: 0,
            maximumFractionDigits: 0,
          }).format(props.house.price)}
        </Button>
        {props.house.size != null ? (
          <Button
            type="primary"
            onClick={(event) => props.handleClick("size", props.house.size)}
          >
            {props.house.size} m²
          </Button>
        ) : (
          ""
        )}
        {props.house.groundArea != null ? (
          <Button
            type="primary"
            onClick={(event) =>
              props.handleClick("ground", props.house.groundArea)
            }
          >
            {props.house.groundArea} m²
          </Button>
        ) : (
          ""
        )}
        {props.house.rooms != null ? (
          <Button
            type="primary"
            onClick={(event) => props.handleClick("rooms", props.house.rooms)}
          >
            {props.house.rooms} rooms
          </Button>
        ) : (
          ""
        )}
        {props.house.objectType != null ? (
          <Button
            type="primary"
            onClick={(event) =>
              props.handleClick("type", props.house.objectType)
            }
          >
            {props.house.objectType}
          </Button>
        ) : (
          ""
        )}
        <Button
          type="primary"
          onClick={(event) => props.removeHouse(props.house)}
        >
          <img src="/img/remove.png" alt="Remove" height="13px" />
        </Button>
      </div>
      <div style={{ float: "left" }}>
        <Button
          type="primary"
          onClick={(event) =>
            props.changeLocation(
              props.house.stateName,
              props.house.districtName,
              props.house.locationName
            )
          }
        >
          {props.house.postalCode}
        </Button>
        <Button
          type="primary"
          onClick={(event) =>
            props.changeLocation(
              props.house.stateName,
              props.house.districtName,
              props.house.locationName
            )
          }
        >
          {props.house.locationName}
        </Button>
        <Button
          type="primary"
          onClick={(event) =>
            props.changeDistrict(
              props.house.stateName,
              props.house.districtName
            )
          }
        >
          {props.house.districtName}
        </Button>
        <Button
          type="primary"
          onClick={(event) => props.changeState(props.house.stateName)}
        >
          {props.house.stateName}
        </Button>
      </div>
      <div style={{ float: "left" }}>
        <Button
          type="primary"
          onClick={(event) =>
            props.handleClick(
              "postedAfter",
              props.house.editDate.replaceAll(".", "")
            )
          }
        >
          {props.house.editDate}
        </Button>
      </div>
      <div
        style={{
          float: "right",
          textAlign: "right",
          fontSize: "10px",
        }}
      >
        <a
          href={props.getMapsLink(props.house)}
          rel="noopener noreferrer"
          target="_blank"
        >
          <img src="/img/maps_icon.png" alt="Map" height="20px" />
        </a>
        <FacebookShareButton url={props.getShareLink(props.house)}>
          <FacebookIcon size="20"></FacebookIcon>
        </FacebookShareButton>
        <WhatsappShareButton url={props.house.link}>
          <WhatsappIcon size="20"></WhatsappIcon>
        </WhatsappShareButton>
        <EmailShareButton url={props.house.link}>
          <EmailIcon size="20"></EmailIcon>
        </EmailShareButton>
      </div>
    </div>
  );
}

export default getHouseData;
