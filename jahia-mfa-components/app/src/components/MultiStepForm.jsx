import React from "react";
import { useForm, useStep } from "react-hooks-helper";
import LoginForm from "./LoginForm";
import MFAmessage from "./MFAmessage";
import ActivateMFA from "./ActivateMFA";
import PrepareMFA from "./PrepareMFA";
import DeactivateMFA from "./DeactivateMFA";
import ViewQRCode from "./ViewQRCode";
import "./styles.css";

const steps = [
  { id: "login" },
  {id: "prepareMFA"},
  { id: "login" },
  {id: "activateMFA"},
  { id: "login" },
  {id: "deactivateMFA"},
  { id: "login" },
  {id: "viewQRCode"},
  { id: "login" },
  { id: "success" }
];

const defaultData = {
  username: "anne",
  password: "password",
};
const Buffer = require('buffer').Buffer;
const headers = {   'Authorization': 'Basic ' + Buffer.from(defaultData.username+":"+defaultData.password).toString("base64"),
  'Content-Type': 'application/json'
}
const MultiStepForm = (client) => {
  const [formData, setForm] = useForm(defaultData);
  const { step, navigation } = useStep({ initialStep: 0, steps });
  const { id } = step;


  const props = { client, formData, setForm, navigation, headers };

  switch (id) {
    case "login":
      return <LoginForm   {...props} />;
    case "prepareMFA":
      return <PrepareMFA {...props} />;
    case "activateMFA":
      return <ActivateMFA {...props} />;
    case "deactivateMFA":
      return <DeactivateMFA {...props} />;
    case "viewQRCode":
      return <ViewQRCode {...props} />;
    case "success":
      return <MFAmessage {...props} />;
    default:
      return null;
  }
};

export default MultiStepForm;
