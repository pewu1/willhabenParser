import React from "react";
import { Button } from "antd";

function getButton(props) {
  return props.param != null ? (
    <Button
      type="primary"
      onClick={(event) => props.handleClick("size", props.param)}
    >
      {props.house.size} m²
    </Button>
  ) : (
    ""
  );
}

export default getButton;
