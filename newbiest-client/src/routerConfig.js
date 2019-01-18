import Home from './pages/Home';

import BlankLayout from './layouts/BlankLayout';
import Login from './pages/Login';

import HeaderAsideFooterResponsiveLayout from './layouts/HeaderAsideFooterResponsiveLayout';

import NotFound from './pages/NotFound';
import EntityProperties from './pages/Properties';
import UserProperties from './pages/Properties/components/userProperties/UserProperties';
import RoleProperties from './pages/Properties/components/RoleProperties';

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
    path: 'System/OnlineTableManager/:tableRrn',
    layout: HeaderAsideFooterResponsiveLayout,
    component: EntityProperties,
  },
  {
    path: 'System/OnlineTabManager/:tableRrn',
    layout: HeaderAsideFooterResponsiveLayout,
    component: EntityProperties,
  },
  {
    path: 'System/OnlineFieldManager/:tableRrn',
    layout: HeaderAsideFooterResponsiveLayout,
    component: EntityProperties,
  },
  {
    path: 'System/OnlineRefTableManager/:tableRrn',
    layout: HeaderAsideFooterResponsiveLayout,
    component: EntityProperties,
  },
  {
    path: 'System/SysRefNameManager/:tableRrn',
    layout: HeaderAsideFooterResponsiveLayout,
    component: EntityProperties,
  },
  {
    path: 'System/OrgRefNameManager/:tableRrn',
    layout: HeaderAsideFooterResponsiveLayout,
    component: EntityProperties,
  },
  {
    path: 'System/MessageManager/:tableRrn',
    layout: HeaderAsideFooterResponsiveLayout,
    component: EntityProperties,
  },

  {
    path: 'Security/UserManager/:tableRrn',
    layout: HeaderAsideFooterResponsiveLayout,
    component: UserProperties,
  },
  {
    path: 'Security/RoleManager/:tableRrn',
    layout: HeaderAsideFooterResponsiveLayout,
    component: RoleProperties,
  },
  
  
  {
    path: 'Rms/EquipmentManager/:tableRrn',
    layout: HeaderAsideFooterResponsiveLayout,
    component: EntityProperties,
  },
  {
    path: 'Rms/RecipeManager/:tableRrn',
    layout: HeaderAsideFooterResponsiveLayout,
    component: EntityProperties,
  },
  {
    path: '*',
    layout: HeaderAsideFooterResponsiveLayout,
    component: NotFound,
  },
];

export default routerConfig;
