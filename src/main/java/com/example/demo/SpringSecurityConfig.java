package com.example.demo;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.auth.handler.LoginSuccesHandler;

@EnableGlobalMethodSecurity(securedEnabled = true)
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private LoginSuccesHandler successHandler;

	@Autowired
	private DataSource dataSource;

	@Bean
	public BCryptPasswordEncoder passwordEnconder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	public void configurerGlobal(AuthenticationManagerBuilder builder) throws Exception {
		PasswordEncoder encoder = passwordEnconder();
		/*
		 * UserBuilder users = User.builder().passwordEncoder(encoder::encode);
		 * builder.inMemoryAuthentication().withUser(users.username("admin").password(
		 * "12345").roles("ADMIN", "USER"))
		 * .withUser(users.username("mario").password("12345").roles("USER"));
		 */

		builder.jdbcAuthentication().dataSource(dataSource).passwordEncoder(encoder)
				.usersByUsernameQuery("select username,password,enabled from users where username=?")
				.authoritiesByUsernameQuery(
						"select u.username,a.authority from authorities a inner join users  u on(a.user_id=u.id) where u.username=?");

	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/", "/css/**", "/js/**", "/images/**", "/listar**","/locale","/api/clientes/**").permitAll()
				/*
				 * .antMatchers("/ver/**").hasAnyRole("USER")
				 * .antMatchers("/uploads/**").hasAnyRole("USER")
				 * .antMatchers("/form/**").hasAnyRole("ADMIN").antMatchers("/eliminar/**").
				 * hasAnyRole("ADMIN") .antMatchers("/factura/**").hasAnyRole("ADMIN")
				 */.anyRequest().authenticated().and().formLogin().successHandler(successHandler).loginPage("/login")
				.permitAll().and().logout().permitAll().and().exceptionHandling().accessDeniedPage("/error_403");
	}

}
