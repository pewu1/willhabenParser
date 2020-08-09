import React from "react";

function displayLoader() {
  return (
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
  );
}

export default displayLoader;
