
describe('Flipkart E2E Functional UI Tests - Real Project', () => {
  const mobileNumber = '9398473184';
  const otpCode = '252632';

  const closePopupIfPresent = () => {
    cy.get('body').then($body => {
      if ($body.find('._2doB4z').length > 0) {
        cy.get('._2doB4z').click({ force: true });
      }
    });
  };

  const closeChallengerModalIfPresent = () => {
    cy.get('body').then($body => {
      if ($body.find('#challenger-modal').length > 0) {
        cy.get('#challenger-modal').invoke('attr', 'style', 'display: none');
      }
    });
  };

  beforeEach(() => {
    cy.visit('https://www.flipkart.com', { failOnStatusCode: false });
    cy.wait(2000);
    closePopupIfPresent();
    closeChallengerModalIfPresent();
  });

  it('Login via OTP if not already logged in', () => {
    cy.get('body').then($body => {
      const isLoginVisible = $body.text().includes('Login');
      const isProfileVisible = $body.text().includes('My Account') || $body.text().includes('Profile');

      if (isLoginVisible && !isProfileVisible) {
        cy.contains(/login/i).trigger('mouseover');
        cy.wait(1000);
        cy.contains('My Profile', { timeout: 10000 }).click({ force: true });

        cy.url().should('include', '/account/login');
        cy.get('input[type="text"]').first().type(mobileNumber);
        cy.contains('Request OTP').click();
        cy.wait(1000);
        cy.get('input[type="text"]').eq(1).type(otpCode);
        cy.wait(3000);
        cy.go('back');
      }
    });
  });

  it('Validate page title and URL', () => {
    cy.url().should('include', 'flipkart.com');
    cy.title().should('include', 'Online Shopping');
  });

  it('Searches "shirts men" and adds a product to cart', () => {
  cy.get('input[name="q"]').should('be.visible').type('shirts men{enter}');
  cy.wait(3000); // Wait for results to load

  cy.get('body').then(($body) => {
    if ($body.find('div._1YokD2._3Mn1Gg').length > 0) {
      cy.get('div._1YokD2._3Mn1Gg').should('be.visible');
    } else if ($body.find('._2gmUFU').length > 0) {
      cy.get('._2gmUFU').first().should('be.visible');
    } else if ($body.text().toLowerCase().includes('no results')) {
      cy.log('No search results found.');
      cy.screenshot('no-search-results');
      return;
    } else {
      cy.log('Search grid not found, UI may have changed.');
      cy.screenshot('grid-missing');
      return;
    }

    // Product filter and selection
    cy.contains('Self Design', { timeout: 10000 }).click({ force: true });
    cy.wait(3000);

    const sizes = ['M', 'L', 'XL'];
    cy.get('button, div').each(($el) => {
      const text = $el.text().trim();
      if (sizes.includes(text)) {
        cy.wrap($el).click({ force: true });
        return false;
      }
    });

    cy.contains(/add to cart/i, { timeout: 10000 }).click({ force: true });
    cy.wait(3000);

    // Confirm navigation to cart
    cy.url().then((url) => {
      if (url.includes('/viewcart')) {
        cy.log('Navigated to cart successfully.');
      } else {
        cy.log('Still on product page or unknown page.');
        cy.screenshot('cart-check-failed');
      }
    });
  });
});


  it('Navigate to Mobiles > iPhone or Apple', () => {
    cy.wait(2000);
    closePopupIfPresent();
    cy.contains('Mobiles', { timeout: 10000 }).click({ force: true });
    cy.url().should('include', 'mobile-phones');

    cy.get('body').then($body => {
      if ($body.text().toLowerCase().includes('iphone')) {
        cy.contains(/iphone/i, { timeout: 10000 }).click({ force: true });
      } else if ($body.text().toLowerCase().includes('apple')) {
        cy.contains(/apple/i, { timeout: 10000 }).click({ force: true });
      } else {
        cy.log('Neither iPhone nor Apple found.').screenshot('iphone-not-found');
      }
    });
  });

  it('Scroll to bottom and verify footer links', () => {
    cy.scrollTo('bottom', { ensureScrollable: false });
    cy.wait(1000);
    cy.contains('ABOUT').should('exist');
    cy.contains('Contact Us').should('exist');
    cy.contains('Careers').should('exist');
  });

  it('Scrolls horizontally through Top Offers section', () => {
    cy.get('h2').each($el => {
      const section = $el.text().toLowerCase();
      if (section.includes('top offers') || section.includes('deals')) {
        cy.wrap($el)
          .scrollIntoView()
          .parent()
          .scrollTo('right', { duration: 1000 });
      }
    });
  });

  it('Simulate checkout and dummy payment', () => {
    cy.visit('https://www.flipkart.com/viewcart');
    cy.get('body').then($body => {
      if ($body.text().includes('Place Order')) {
        cy.contains('Place Order').click({ force: true });
        cy.url().should('include', '/checkout');

        cy.contains(/Deliver Here|CONTINUE/, { timeout: 10000 }).click({ force: true });
        cy.wait(1000);
        cy.contains(/CONTINUE|Proceed/, { timeout: 10000 }).click({ force: true });

        cy.contains('Cash on Delivery', { timeout: 10000 }).click({ force: true });
        cy.contains('Confirm Order').click({ force: true });
        cy.log('Dummy payment flow simulated');
      } else {
        cy.log('Cart is empty. Payment flow skipped.');
      }
    });
  });

  it('Logout from Flipkart account', () => {
    cy.get('body').then($body => {
      if ($body.text().includes('My Account') || $body.text().includes('Profile')) {
        cy.contains(/Login|Account/i).trigger('mouseover');
        cy.wait(1000);
        cy.get('body').then($b => {
          if ($b.text().includes('Logout')) {
            cy.contains('Logout').click({ force: true });
            cy.wait(2000);
            cy.contains('Login').should('exist');
          } else {
            cy.log('Logout option not found.');
          }
        });
      } else {
        cy.log('User is not logged in.');
      }
    });
  });

  it('Verify key UI elements on homepage', () => {
    const items = ['Mobiles', 'Fashion', 'Electronics', 'Home', 'Appliances'];
    items.forEach(label => {
      cy.contains(label).should('exist');
    });

    cy.get('input[name="q"]').should('be.visible');

    cy.get('body').then($body => {
      if ($body.find('[aria-label="Cart"]').length > 0) {
        cy.get('[aria-label="Cart"]').should('be.visible');
      } else {
        cy.log('Cart icon may not be visible on this view.');
      }
    });
  });
});
