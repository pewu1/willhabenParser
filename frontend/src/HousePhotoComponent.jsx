import React from "react";

function renderHousePhoto(props) {
  return (
    <div>
      <a href={props.house.link} target="_blank" rel="noopener noreferrer">
        <img
          src={
            props.house.pictureLink != null
              ? props.house.pictureLink
              : "https://upload.wikimedia.org/wikipedia/commons/a/ac/No_image_available.svg"
          }
          width="100%"
          style={{ maxHeight: "400px" }}
          alt={props.house.link}
          data-action={props.house.link}
        />
      </a>
    </div>
  );
}

export default renderHousePhoto;
