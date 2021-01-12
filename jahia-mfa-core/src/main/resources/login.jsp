<%@page language="java" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<%@ page import="org.jahia.settings.SettingsBean" %>
<%@ taglib uri="http://www.jahia.org/tags/internalLib" prefix="internal" %>
<%@ taglib prefix="ui" uri="http://www.jahia.org/tags/uiComponentsLib" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions"%>

<utility:setBundle basename="JahiaInternalResources"/>
<html>
    <% pageContext.setAttribute("isFullReadOnly", Boolean.valueOf(SettingsBean.getInstance().isFullReadOnlyMode())); %>
    <head>
        <!-- Meta info -->
        <title><fmt:message key="label.login"/></title>
        <meta name="description" content=""/>
        <meta name="keywords" content="">
        <meta charset="UTF-8">
        <meta name="robots" content="noindex, nofollow"/>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="shortcut icon" href="/favicon.ico">

        <!-- Import font (Nunito Sans) -->
        <link href="/css/loginFont.css" rel="stylesheet">
        <link href="/modules/jahia-mfa-core/css/mfa.css" rel="stylesheet">
        <!-- jQuery -->
        <script src="/css/jquery.min.js"></script>
        <script src="/modules/jahia-mfa-core/javascript/mfa.js"></script>
        <!-- Main style -->
        <link rel="stylesheet" href="/css/loginMain_dark.css">
    </head>
    <body>
        <section class="login" style="background-image: url(/css/images/Background_Login-01.png);">
            <div class="login-main">
                <div class="position-container">

                    <div class="logo">
                        <img src="/css/images/dx_logo.png" alt="jahia logo">
                    </div>

                    <div class="login-form">
                        <ui:loginArea action="/cms/login">

                            <ui:isLoginError var="loginResult">
                                <div class="login-error">
                                    <c:choose>
                                        <c:when test="${loginResult == 'account_locked'}">
                                            <fmt:message key="message.accountLocked"/>
                                        </c:when>
                                        <c:when test="${loginResult == 'logged_in_users_limit_reached'}">
                                            <fmt:message key="message.loggedInUsersLimitReached"/>
                                        </c:when>
                                        <c:otherwise>
                                            <fmt:message key="message.invalidUsernamePassword"/>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="group">
                                    <input type="text" name="username" verify="/${pageContext.request.locale.language}/sites/${functions:currentSiteKey(pageContext.request)}.verifyMFAEnforcementAction.do" maxlength="250" value="${fn:escapeXml(param['username'])}"
                                           required />
                                    <span class="highlight"></span>
                                    <span class="bar"></span>
                                    <label class="inputlabel"><fmt:message key="label.username"/> </label>
                                </div>
                                <div class="group">
                                    <input type="password" name="password" maxlength="250" autocomplete="off" required />
                                    <span class="highlight"></span>
                                    <span class="bar"></span>
                                    <label class="inputlabel"><fmt:message key="label.password"/></label>
                                </div>
                            </ui:isLoginError>

                            <c:if test="${empty loginResult}">
                                <div class="group">
                                    <input type="text" name="username" sitekey="${functions:currentSiteKey(pageContext.request)}" maxlength="250" required />
                                    <span class="highlight"></span>
                                    <span class="bar"></span>
                                    <label class="inputlabel"><fmt:message key="label.username"/></label>
                                </div>
                                <div class="group">
                                    <input type="password" name="password" maxlength="250" autocomplete="off" required />
                                    <span class="highlight"></span>
                                    <span class="bar"></span>
                                    <label class="inputlabel"><fmt:message key="label.password"/></label>
                                </div>
                                <div class="group digit-group" id="mfa-field" style="display: none">
                                    <input type="text" id="digit-1" name="digit-1" data-next="digit-2" autocomplete="off" />
                                    <input type="text" id="digit-2" name="digit-2" data-next="digit-3" data-previous="digit-1" autocomplete="off"/>
                                    <input type="text" id="digit-3" name="digit-3" data-next="digit-4" data-previous="digit-2" autocomplete="off"/>
                                    <span class="splitter">&ndash;</span>
                                    <input type="text" id="digit-4" name="digit-4" data-next="digit-5" data-previous="digit-3" autocomplete="off"/>
                                    <input type="text" id="digit-5" name="digit-5" data-next="digit-6" data-previous="digit-4"autocomplete="off" />
                                    <input type="text" id="digit-6" name="digit-6" data-previous="digit-5" autocomplete="off"/>
                                    <label
                                            class="inputlabel"><fmt:message key="label.code"/>
                                        OTP</label>
                                </div>
                            </c:if>

                            <c:if test="${(not isFullReadOnly) and (not fn:contains(param.redirect, '/administration'))}">
                                <label class="check-label no-select">
                                    <ui:loginRememberMe name="checkbox" class="fs1" />
                                    <lb><fmt:message key="label.rememberme"/></lb>
                                </label>
                            </c:if>

                            <button type="submit"><fmt:message key='label.login'/></button>

                        </ui:loginArea>
                    </div>

                </div>
                <div class="login-footer">Jahia - Copyrights © 2002-2020 All Rights Reserved by Jahia Solutions Group SA.</div>
            </div>
        </section>

    </body>
</html>