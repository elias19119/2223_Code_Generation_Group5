package io.swagger.configuration;

import io.swagger.Security.CustomAuthenticationProvider;
import io.swagger.Service.MyUserDetailsService;
import io.swagger.annotations.ApiKeyAuthDefinition;
import io.swagger.annotations.SecurityDefinition;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.filter.JwtTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@SwaggerDefinition(securityDefinition = @SecurityDefinition(apiKeyAuthDefinitions = {@ApiKeyAuthDefinition(key = "X-Auth-Token",
        in = ApiKeyAuthDefinition.ApiKeyLocation.HEADER, name = "X-Auth-Token")}))
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenFilter jwtTokenFilter;
    private final MyUserDetailsService userDetailsService;

    public WebSecurityConfig(JwtTokenFilter jwtTokenFilter, MyUserDetailsService userDetailsService) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    public CustomAuthenticationProvider authProvider() {
        CustomAuthenticationProvider authenticationProvider = new CustomAuthenticationProvider(userDetailsService);
        return authenticationProvider;
    }
    @Override
    protected void configure(HttpSecurity security) throws Exception
    {
        security.cors().and().csrf().disable();
        security.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        security.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        security.authorizeRequests()
                .antMatchers("/login")
                .permitAll()
                .antMatchers("/users/")
                .permitAll()
                .antMatchers("/accounts/")
                .permitAll()
                .antMatchers("/transactions/")
                .permitAll()
                .antMatchers("/users/accounts/")
                .permitAll()
                .antMatchers("/swagger-ui/**/**")
                .permitAll()
                .antMatchers("/h2-console/**/**")
                .permitAll()
                .antMatchers("/swagger-resources/**")
                .permitAll()
                .antMatchers("/swagger-ui.html")
                .permitAll()
                .antMatchers("/api-docs")
                .permitAll()
                .anyRequest().authenticated();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:8082/"); // Replace "*" with the actual allowed origin
        configuration.addAllowedMethod("*"); // Add the allowed HTTP methods
        configuration.addAllowedHeader("*"); // Add the allowed headers
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    @Autowired
    public void configure (AuthenticationManagerBuilder authBuilder) throws Exception {

        authBuilder.authenticationProvider(authProvider());
    }
}