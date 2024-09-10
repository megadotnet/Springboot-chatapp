// 导入React Router库的钩子和组件
import {
  Redirect,
  Route,
  BrowserRouter as Router,
  Switch,
  useHistory,
} from "react-router-dom";
// 导入登录页面和聊天页面组件
import { Login } from "./Layout/Login";
import { ChatPage2 } from "./Layout/ChatPage2";

// 定义App组件
// 该组件负责管理应用的路由逻辑，并根据用户是否已登录进行重定向
function App() {
  // 使用useHistory钩子获取React Router的history对象
  const history = useHistory();

  // 判断本地存储中是否存在已登录的用户名
  // 如果存在，则重定向到聊天页面，否则重定向到登录页面
  if (localStorage.getItem("chat-username")) {
    history.push("/chat");
  } else {
    history.push("/login");
  }

  // 返回应用的路由配置
  return (
    <Router>
      <Switch>
        // 定义根路径的重定向逻辑
        <Route exact path={"/"}>
          <Redirect to={"/login"}></Redirect>
        </Route>
        // 定义登录页面的路由
        <Route path={"/login"}>
          <Login></Login>
        </Route>
        // 定义聊天页面的路由
        <Route path={"/chat"}>
          <ChatPage2></ChatPage2>
        </Route>
      </Switch>
    </Router>
  );
}

// 导出App组件作为应用的主组件
export default App;
