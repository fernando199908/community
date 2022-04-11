package com.julymelon.community.controller;

import com.julymelon.community.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/data")
    @ResponseBody
    public String getData() {
        return alphaService.find();
    }

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "Hello Spring Boot!!";
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ": " + value);
        }
        int code = Integer.parseInt(request.getParameter("code"));
        System.out.println(code);

        // 返回响应数据
        if (code == 3) {
            response.setContentType("text/html;charset=utf-8");
            try (
                    PrintWriter writer = response.getWriter();
            ) {
                writer.write("<h1>牛客网</h1>");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //    get请求
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(@RequestParam(name = "current", required = false, defaultValue = "1") int current,
                              @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id) {
        System.out.println(id);
        return "a student";
    }

    //    post请求
//    @RequestMapping(path = "/student", method = RequestMethod.POST)
//    @ResponseBody
//    public String saveStudent(@RequestParam(name = "name", required = false, defaultValue = "hahaha") String name,
//                              @RequestParam(name = "age", required = false, defaultValue = "10") int age) {
//        System.out.println(name);
//        System.out.println(age);
//        return "success";
//    }

    @RequestMapping(path = "/student", method = RequestMethod.POST)
    public ModelAndView saveStudent(@RequestParam(name = "name", required = false, defaultValue = "hahaha") String name,
                                    @RequestParam(name = "age", required = false, defaultValue = "10") int age) {
        System.out.println(name);
        System.out.println(age);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name", name);
        modelAndView.addObject("age", age);
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }

    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name", "张三");
        modelAndView.addObject("age", 23);
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }

    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model) {
        model.addAttribute("name", "北京大学");
        model.addAttribute("age", 120);
        return "/demo/view";
    }


    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp() {
        Map<String, Object> emp = new HashMap<String, Object>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 8000.00);
        return emp;
    }


    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps() {
        List<Map<String, Object>> emps = new ArrayList<>();
        Map<String, Object> emp = new HashMap<String, Object>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 8000.00);
        emps.add(emp);

        emp = new HashMap<String, Object>();
        emp.put("name", "李四");
        emp.put("age", 24);
        emp.put("salary", 6000.00);
        emps.add(emp);

        emp = new HashMap<String, Object>();
        emp.put("name", "王五");
        emp.put("age", 25);
        emp.put("salary", 10000.00);
        emps.add(emp);

        return emps;
    }

}