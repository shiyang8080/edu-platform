import { createRouter, createWebHistory } from 'vue-router'
import { getToken, getRole } from '@/utils/auth'

// Layout
const MainLayout = () => import('@/components/layout/MainLayout.vue')

// Views
const Login = () => import('@/views/login/Login.vue')
const Register = () => import('@/views/login/Register.vue')
const Dashboard = () => import('@/views/dashboard/Dashboard.vue')
const UserList = () => import('@/views/user/UserList.vue')
const CourseList = () => import('@/views/course/CourseList.vue')
const CourseDetail = () => import('@/views/course/CourseDetail.vue')
const MySelections = () => import('@/views/selection/MySelections.vue')
const SelectCourse = () => import('@/views/selection/SelectCourse.vue')
const MyGrades = () => import('@/views/grade/MyGrades.vue')
const CourseGrades = () => import('@/views/grade/CourseGrades.vue')
const GradeStatistics = () => import('@/views/grade/GradeStatistics.vue')
const NoticeList = () => import('@/views/notice/NoticeList.vue')
const NoticeDetail = () => import('@/views/notice/NoticeDetail.vue')
const ProfilePage = () => import('@/views/profile/ProfilePage.vue')

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login, meta: { noAuth: true } },
  { path: '/register', component: Register, meta: { noAuth: true } },
  {
    path: '/',
    component: MainLayout,
    children: [
      { path: 'dashboard', component: Dashboard },
      { path: 'users/list', component: UserList, meta: { roles: ['ADMIN'] } },
      { path: 'courses/list', component: CourseList },
      { path: 'courses/detail/:id', component: CourseDetail },
      { path: 'selections/my', component: MySelections, meta: { roles: ['STUDENT'] } },
      { path: 'selections/available', component: SelectCourse, meta: { roles: ['STUDENT'] } },
      { path: 'grades/my', component: MyGrades, meta: { roles: ['STUDENT'] } },
      { path: 'grades/course/:id', component: CourseGrades, meta: { roles: ['TEACHER', 'ADMIN'] } },
      { path: 'grades/statistics', component: GradeStatistics },
      { path: 'notices/list', component: NoticeList },
      { path: 'notices/detail/:id', component: NoticeDetail },
      { path: 'profile', component: ProfilePage }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard
router.beforeEach((to, _from, next) => {
  const token = getToken()
  if (to.meta.noAuth) {
    if (token && to.path === '/login') {
      next('/dashboard')
    } else {
      next()
    }
  } else {
    if (!token) {
      next('/login')
      return
    }
    const role = getRole()
    if (to.meta.roles && !to.meta.roles.includes(role)) {
      next('/dashboard')
    } else {
      next()
    }
  }
})

export default router
