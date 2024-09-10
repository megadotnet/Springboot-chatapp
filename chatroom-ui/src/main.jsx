// 导入React库，用于构建用户界面
import React from "react";
// 导入ReactDOM库，用于将React元素渲染到HTML文档中
import ReactDOM from "react-dom/root";
// 导入App组件，作为应用程序的入口
import App from "./App.jsx";
// 导入BrowserRouter，用于实现客户端路由
import { BrowserRouter } from "react-router-dom";

// 使用ReactDOM的createRoot方法来创建React根元素
// 然后渲染一个BrowserRouter包裹的App组件到HTML文档中的root元素
// 这里使用BrowserRouter是为了让应用支持URL路由功能
ReactDOM.createRoot(document.getElementById("root")).render(
  <BrowserRouter>
    <App />
  </BrowserRouter>
);
