import React from "react";

import ItemForm from "./ItemForm";

const LoginForm = ({ setForm, formData, navigation }) => {
  const { username, password } = formData;

  const { next } = navigation;

  return (

    <div className="form">
        <div>
      <ItemForm
        label="Username"
        name="username"
        value={username}
        onChange={setForm}
      />
      <ItemForm
        label="Password"
        name="password"
        value={password}
        onChange={setForm}
      />
        </div>
        <button onClick={next}>Login</button>
    </div>
  );
};

export default LoginForm;
