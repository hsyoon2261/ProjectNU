//package org.example.projectnu.common.interceptor
//
//import jakarta.servlet.http.HttpServletRequest
//import jakarta.servlet.http.HttpServletResponse
//import org.springframework.web.servlet.HandlerInterceptor
//import org.springframework.web.servlet.ModelAndView
//
//class CaptureSessionInterceptor : HandlerInterceptor {
//    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
//        val jsessionId = request.getHeader("cookie")?.split(";")?.find { it.startsWith("JSESSIONID=") }
//        request.setAttribute("JSESSIONID", jsessionId)
//
//        return true
//    }
//
//    override fun postHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any, modelAndView: ModelAndView?) {
//        // not implemented
//    }
//
//    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
//        // not implemented
//    }
//}
