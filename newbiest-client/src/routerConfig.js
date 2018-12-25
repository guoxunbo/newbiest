import Home from './pages/Home';

import BlankLayout from './layouts/BlankLayout';
import Login from './pages/Login';

import ListTableManager from './pages/ListTableManager';
import HeaderAsideFooterResponsiveLayout from './layouts/HeaderAsideFooterResponsiveLayout';

import NotFound from './pages/NotFound';
import EntityProperties from './pages/Properties';
import UserProperties from './pages/Properties/components/userProperties/UserProperties';

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
    path: 'System/DynamicTable/:tableRrn',
    layout: HeaderAsideFooterResponsiveLayout,
    component: ListTableManager,
  },
  {
    path: 'Security/DynamicTable/:tableRrn',
    layout: HeaderAsideFooterResponsiveLayout,
    component: UserProperties,
  },
  {
    path: 'System/DynamicTab/:tableRrn',
    layout: HeaderAsideFooterResponsiveLayout,
    component: ListTableManager,
  },
  {
    path: 'System/DynamicField/:tableRrn',
    layout: HeaderAsideFooterResponsiveLayout,
    component: ListTableManager,
  },
  {
    path: '*',
    layout: HeaderAsideFooterResponsiveLayout,
    component: NotFound,
  },
];

export default routerConfig;
