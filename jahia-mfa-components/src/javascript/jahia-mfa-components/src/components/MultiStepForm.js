import React from "react";
import { useForm, useStep } from "react-hooks-helper";

import LoginForm from "./LoginForm";
import MFAManagement from "./MFAManagement";
import Review from "./Review";
import Submit from "./Submit";

import "./styles.css";

const steps = [
  { id: "login" },
  { id: "manageMFA" },
  { id: "review" },
  { id: "submit" }
];

const defaultData = {
  username: "",
  password: "",
  nickName: "Jan",
  address: "200 South Main St",
  city: "Anytown",
  state: "CA",
  zip: "90505",
  email: "email@domain.com",
  phone: "+61 4252 454 332"
};

const MultiStepForm = () => {
  const [formData, setForm] = useForm(defaultData);
  const { step, navigation } = useStep({ initialStep: 0, steps });
  const { id } = step;

  const props = { formData, setForm, navigation };

  switch (id) {
    case "login":
      return <LoginForm {...props} />;
    case "manageMFA":
      return <MFAManagement {...props} />;
    case "review":
      return <Review {...props} />;
    case "submit":
      return <Submit {...props} />;
    default:
      return null;
  }
};

export default MultiStepForm;
