import React, { useContext, useState } from "react";
import { useHistory } from "react-router-dom";
import "./button.css";

/**
 * 登录组件，用于用户登录聊天应用
 *
 * 该组件使用React Hooks（useHistory和useState）以及react-router-dom库来处理用户登录流程
 * 用户可以在输入框中输入用户名，并通过按下回车键或点击登录按钮进行登录
 * 登录后，用户名将被存储在localStorage中，并重定向用户到聊天界面
 */
export const Login = () => {
  // 获取history对象，用于程序中手动控制路由的跳转
  const history = useHistory();
  // 使用useState管理用户名状态
  const [username, setUsername] = useState();

  /**
   * 处理登录逻辑
   *
   * 将用户名存储到localStorage中，并引导用户到聊天页面
   */
  const handleLogin = () => {
    localStorage.setItem("chat-username", username);

    history.push("/chat");
  };

  // 渲染登录界面
  return (
    <div
      className=" d-flex align-items-center justify-content-center"
      style={{
        height: "100vh",
        backgroundImage: `url("https://picsum.photos/1536/735?grayscale")`,
        backgroundRepeat: "no-repeat",
        backgroundSize: "cover",
      }}
    >
      <div className="container form-group w-25  d-flex align-items-center justify-content-center gap-2">
        <input
          type="text"
          name=""
          id=""
          className="rounded-3 form-control"
          placeholder="Name"
          onChange={(e) => setUsername(e.target.value)}
          onKeyUp={(e) => {
            // 检测按键，如果是Enter键则触发登录操作
            if (e.key === "Enter" || e.key == 13) handleLogin();
          }}
        />
        <button type="button" value={"Connect"} onClick={handleLogin}>
          <span>Connect</span>
        </button>
      </div>
    </div>
  );
};
