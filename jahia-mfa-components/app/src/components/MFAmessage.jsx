import React from "react";
const MFAmessage = ({ navigation }) => {
  const { go } = navigation;
  return (
    <div>
      <h3>Your MFA has been succesfully activated</h3>
      <button onClick={() => go("login")}>Back</button>
    </div>
  );
};

export default MFAmessage;
