import Home from './pages/Home';

import BlankLayout from './layouts/BlankLayout';
import Login from './pages/Login';

import ListTableManager from './pages/ListTableManager';
import HeaderAsideFooterResponsiveLayout from './layouts/HeaderAsideFooterResponsiveLayout';

import Page6 from './pages/Page6';
import NotFound from './pages/NotFound';

const routerConfig = [
  {
    path: '/',
    layout: BlankLayout,
    component: Login,
  },
  {
    path: '/Home',
    layout: HeaderAsideFooterResponsiveLayout,
    component: Home,
  },
  {
    path: '/DynamicTable/:tableRrn',
    layout: HeaderAsideFooterResponsiveLayout,
    component: ListTableManager,
  },
  {
    path: '/ListTableManager',
    layout: HeaderAsideFooterResponsiveLayout,
    component: ListTableManager,
  },
  {
    path: '/page6',
    layout: HeaderAsideFooterResponsiveLayout,
    component: Page6,
  },
  {
    path: '*',
    layout: HeaderAsideFooterResponsiveLayout,
    component: NotFound,
  },
];

export default routerConfig;
