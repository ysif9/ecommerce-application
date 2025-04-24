import {MainLayout} from "@/components/Layout/MainLayout";
import {Card, CardContent} from "@/components/ui/card";

const AboutPage = () => {
  return (
      <MainLayout>
        <div className="container py-12">
          <div className="max-w-4xl mx-auto">
            <h1 className="text-4xl font-bold mb-8">About EchoCart</h1>

            <Card className="mb-8">
              <CardContent className="p-6">
                <div className="grid md:grid-cols-2 gap-8">
                  <div>
                    <h2 className="text-2xl font-semibold mb-4">Our Story</h2>
                    <p className="text-gray-600 leading-relaxed mb-4">
                      Welcome to EchoCart, your one-stop destination for quality products and exceptional shopping experiences. Founded with a vision to revolutionize online shopping, we strive to provide our customers with a seamless and enjoyable shopping journey.
                    </p>
                    <p className="text-gray-600 leading-relaxed">
                      Our commitment to customer satisfaction, product quality, and innovative solutions sets us apart in the e-commerce landscape.
                    </p>
                  </div>
                  <div className="relative h-[300px] rounded-lg overflow-hidden">
                    <img
                        src="https://images.unsplash.com/photo-1674027392842-29f8354e236c"
                        alt="EchoCart Store"
                        className="absolute inset-0 w-full h-full object-cover"
                    />
                  </div>
                </div>
              </CardContent>
            </Card>

            <div className="grid md:grid-cols-3 gap-6 mb-8">
              <Card>
                <CardContent className="p-6">
                  <h3 className="text-xl font-semibold mb-3">Quality Products</h3>
                  <p className="text-gray-600">
                    We carefully curate our product selection to ensure the highest quality standards for our customers.
                  </p>
                </CardContent>
              </Card>
              <Card>
                <CardContent className="p-6">
                  <h3 className="text-xl font-semibold mb-3">Fast Delivery</h3>
                  <p className="text-gray-600">
                    Experience swift and reliable delivery services to get your products when you need them.
                  </p>
                </CardContent>
              </Card>
              <Card>
                <CardContent className="p-6">
                  <h3 className="text-xl font-semibold mb-3">24/7 Support</h3>
                  <p className="text-gray-600">
                    Our dedicated customer support team is always ready to assist you with any queries.
                  </p>
                </CardContent>
              </Card>
            </div>

            <Card>
              <CardContent className="p-6">
                <h2 className="text-2xl font-semibold mb-4">Contact Us</h2>
                <p className="text-gray-600 mb-4">
                  Have questions or suggestions? We'd love to hear from you. Reach out to us at:
                </p>
                <ul className="text-gray-600 space-y-2">
                  <li>Email: support@echocart.com</li>
                  <li>Phone: (20) 122-3013009</li>
                  <li>Address: 123 Commerce Street, Cairo, Egypt</li>
                </ul>
              </CardContent>
            </Card>
          </div>
        </div>
      </MainLayout>
  );
};

export default AboutPage;