import React from "react";
import {Button, Typography} from "@jahia/moonstone";
const MFAmessage = ({ navigation }) => {
  const { go } = navigation;
  return (
    <div>
        <Typography>Your MFA has been succesfully activated</Typography>
        <div><Button label="Back" onClick={() => go("login")}/></div>

    </div>
  );
};

export default MFAmessage;
